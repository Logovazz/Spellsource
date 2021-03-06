package com.hiddenswitch.spellsource.net;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.Strand;
import com.hiddenswitch.spellsource.client.models.*;
import com.hiddenswitch.spellsource.client.models.Invite.StatusEnum;
import com.hiddenswitch.spellsource.net.impl.InviteId;
import com.hiddenswitch.spellsource.net.impl.Sync;
import com.hiddenswitch.spellsource.net.impl.UserId;
import com.hiddenswitch.spellsource.net.impl.util.UserRecord;
import com.hiddenswitch.spellsource.net.models.MatchmakingRequest;
import com.hiddenswitch.spellsource.net.impl.MatchmakingQueueConfiguration;
import io.vertx.core.Closeable;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.streams.WriteStream;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.mongo.UpdateOptions;
import net.demilich.metastone.game.cards.desc.CardDesc;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.hiddenswitch.spellsource.net.impl.Mongo.mongo;
import static com.hiddenswitch.spellsource.net.impl.QuickJson.array;
import static com.hiddenswitch.spellsource.net.impl.QuickJson.json;
import static com.hiddenswitch.spellsource.net.impl.Sync.defer;
import static com.hiddenswitch.spellsource.net.impl.Sync.fiber;
import static io.vertx.core.json.JsonObject.mapFrom;

/**
 * This service allows players to invite each other to private games.
 */
public interface Invites {
	Logger LOGGER = LoggerFactory.getLogger(Invites.class);
	String INVITES = "invites";
	JsonArray PENDING_STATUSES = new JsonArray().add(StatusEnum.UNDELIVERED.getValue()).add(StatusEnum.PENDING.getValue());
	long MAX_PENDING_FRIEND_COUNT = 10;
	long MAX_PENDING_MATCHMAKING_INVITES = 1;
	long DEFAULT_EXPIRY_TIME = 15 * 60 * 1000L;


	/**
	 * Sends clients undelivered invites and marks them as pending.
	 */
	static void handleConnections() {
		Connection.connected((connection, fut) -> {
			var userId = connection.userId();
			connection.addCloseHandler(fiber(v -> {
				// Reject pending challenge invites
				var invites = mongo().findWithOptions(INVITES, json(
						"queueId", json("$ne", null),
						"status", json("$in", PENDING_STATUSES),
						"$or", array(json("fromUserId", userId),
								json("toUserId", userId))), new FindOptions()
						.setFields(json("_id", 1)));
				for (var invite : invites) {
					try {
						deleteInvite(new InviteId(invite.getString("_id")), new UserId(userId));
					} catch (IllegalStateException ex) {
						// Sometime between retrieving the invite and now, the invite was accepted.
						LOGGER.warn("handleConnections endHandler {}: An invite's state was changed between retrieving it and " +
								"canceling/rejecting it due to disconnect.", userId);
					}
				}
				v.complete();
			}));
			defer(v -> {
				try {
					// Expire old invites as sender or recipient
					expireInvites(userId);

					// Notify recipients of all pending invites.
					var invites = mongo().find(INVITES, json("toUserId", userId, "status", json("$in", PENDING_STATUSES)), Invite.class);
					for (var invite : invites) {
						if (invite.getStatus() == StatusEnum.UNDELIVERED) {
							invite.status(StatusEnum.PENDING);
						}

						connection.write(new Envelope().added(new EnvelopeAdded().invite(invite)));
					}

					// Set undelivered to pending
					var ids = new JsonArray(invites.stream().map(Invite::getId).collect(Collectors.toList()));
					// State may have changed in between, only mess with the undelivered/pending ones
					mongo().updateCollectionWithOptions(INVITES,
							json("_id", json("$in", ids), "status", json("$in", PENDING_STATUSES)),
							json("$set",
									json("status", StatusEnum.PENDING.getValue())),
							new UpdateOptions().setMulti(true));
					fut.handle(Future.succeededFuture());
				} catch (RuntimeException any) {
					fut.handle(Future.failedFuture(any));
				}
			});
		});
	}

	/**
	 * Expires invites related to the given user
	 *
	 * @param expire
	 */
	@Suspendable
	static void expireInvites(String expire) {
		var time = System.currentTimeMillis();
		var shouldBeExpiredInvites = mongo().find(INVITES, json(
				"toUserId", expire,
				"status", json("$in", PENDING_STATUSES),
				"expiresAt", json("$lt", time)
		), Invite.class);
		var totalExpired = shouldBeExpiredInvites.size();

		// Cache retrieved connections
		Map<String, WriteStream<Envelope>> connections = new HashMap<>();
		for (var shouldBeExpiredInvite : shouldBeExpiredInvites) {
			var fromConnection = connections.computeIfAbsent(shouldBeExpiredInvite.getFromUserId(), Connection::writeStream);
			fromConnection.write(new Envelope().changed(new EnvelopeChanged().invite(new Invite().id(shouldBeExpiredInvite.getId()).status(StatusEnum.TIMEOUT))));
		}

		shouldBeExpiredInvites = mongo().find(INVITES, json(
				"fromUserId", expire,
				"status", json("$in", PENDING_STATUSES),
				"expiresAt", json("$lt", time)
		), Invite.class);
		totalExpired += shouldBeExpiredInvites.size();
		for (var shouldBeExpiredInvite : shouldBeExpiredInvites) {
			var toConnection = connections.computeIfAbsent(shouldBeExpiredInvite.getToUserId(), Connection::writeStream);
			toConnection.write(new Envelope().changed(new EnvelopeChanged().invite(new Invite().id(shouldBeExpiredInvite.getId()).status(StatusEnum.TIMEOUT))));
		}

		var updateResult = mongo().updateCollectionWithOptions(INVITES,
				json("$and", array(json(
						"status", json("$in", PENDING_STATUSES),
						"expiresAt", json("$lt", time)),
						json("$or", array(
								json("fromUserId", expire),
								json("toUserId", expire)
						)))), json(
						"$set", json("status", StatusEnum.TIMEOUT)
				), new UpdateOptions().setMulti(true));

		if (updateResult.getDocModified() != totalExpired) {
			LOGGER.warn("handleConnections {}: Expired {} documents but should have expired {}", expire, updateResult.getDocModified(), totalExpired);
		}
	}

	/**
	 * Invites a user to be a friend, a match, or both.
	 *
	 * @param request
	 * @param user
	 * @return
	 * @throws SuspendExecution
	 */
	static InviteResponse invite(InvitePostRequest request, UserRecord user) throws SuspendExecution {
		UserRecord toUser;
		Closeable matchmaker = null;
		var queued = false;

		try {
			if (request.getToUserId() != null) {
				// Check if the other player is a friend
				toUser = Accounts.get(request.getToUserId());
				// Throws null pointer exception and will be handled above
				if (!toUser.isFriend(user.getId())) {
					throw new SecurityException("Not friends");
				}
			} else if (request.getToUserNameWithToken() != null) {
				try {
					var tokens = request.getToUserNameWithToken().split("#");
					toUser = mongo().findOne(Accounts.USERS, json("username", tokens[0], "privacyToken", tokens[1]), UserRecord.class);
					// Throws null pointer exception, should be handled in parent
					// Users found this way do not need to be friends
				} catch (IndexOutOfBoundsException ex) {
					throw new RuntimeException("Invalid token and username specification. Should look like username#1234");
				}
			} else {
				throw new IllegalArgumentException("No user ID or username with token specified.");
			}

			// Users can only have 1 outgoing matchmaking invite and 10 outgoing friend invites
			if (request.isFriend()) {
				long friendCount = mongo().count(INVITES,
						json("fromUserId", user.getId(),
								"status", json("$in", PENDING_STATUSES),
								"expiresAt", json("$gt", System.currentTimeMillis()),
								"friendId", toUser.getId()));

				if (friendCount >= MAX_PENDING_FRIEND_COUNT) {
					throw new IllegalStateException(String.format("Cannot have more than %d pending friend invites.", MAX_PENDING_FRIEND_COUNT));
				}
			}

			if (request.getQueueId() != null) {
				long matchmakingInviteCount = mongo().count(INVITES,
						json("fromUserId", user.getId(),
								"status", json("$in", PENDING_STATUSES),
								"expiresAt", json("$gt", System.currentTimeMillis()),
								"queueId", json("$ne", null)));

				if (matchmakingInviteCount >= MAX_PENDING_MATCHMAKING_INVITES) {
					throw new IllegalStateException(String.format("Cannot have more than %d pending matchmaking invites.", MAX_PENDING_MATCHMAKING_INVITES));
				}
			}

			var inviteId = InviteId.create();

			// Configure expiration
			var expiryTime = Calendar.getInstance();
			expiryTime.setTime(new Date());
			expiryTime.add(Calendar.MINUTE, 15);

			var vertx = Vertx.currentContext().owner();

			// Set timer to expire the invite after 15 minutes
			vertx.setTimer(DEFAULT_EXPIRY_TIME, fiber(timerId -> {
				// If the invite hasn't been acted on, expire it
				mongo().updateCollection(INVITES,
						json("_id", inviteId.toString(), "status", json("$in", PENDING_STATUSES)),
						json("$set", json("status", StatusEnum.TIMEOUT.getValue())));
			}));

			var invite = new Invite()
					.id(inviteId.toString())
					.fromName(user.getUsername())
					.toName(toUser.getUsername())
					.fromUserId(user.getId())
					.toUserId(toUser.getId())
					.message(request.getMessage())
					.expiresAt(expiryTime.getTimeInMillis())
					.status(StatusEnum.UNDELIVERED);

			if (request.isFriend()) {
				// This is a friend request
				invite.setFriendId(user.getId());
			}

			if (request.getQueueId() != null || request.getDeckId() != null) {
				// Assert that the player isn't currently in a match.

				if (Matchmaking.getUsersInQueues().containsKey(user.getId())) {
					throw new IllegalStateException("User is currently in a queue already. Dequeue first");
				}

				if (Games.isInGame(new UserId(user.getId()))) {
					throw new IllegalStateException("User is currently in a game already. That game must be ended first.");
				}

				// This is (potentially also) a matchmaking queue request
				// Create a new queue just for this invite.
				// Right now, anyone can wait in any queue, but this is probably the most convenient.
				var customQueueId = user.getUsername() + "." + inviteId + "." + request.getQueueId();
				invite.queueId(customQueueId);

				// The matchmaker will close itself automatically if no one joins after 4s or the
				matchmaker = Matchmaking.startMatchmaker(customQueueId, new MatchmakingQueueConfiguration()
						.setAwaitingLobbyTimeout(30000L)
						.setBotOpponent(false)
						.setEmptyLobbyTimeout(4000L)
						.setLobbySize(2)
						.setName(String.format("%s's private match", user.getUsername()))
						.setOnce(true)
						.setPrivateLobby(true)
						.setRanked(false)
						.setStillConnectedTimeout(2000L)
						.setStartsAutomatically(true)
						.setJoin(true)
						.setRules(new CardDesc[0]));

				// If this point forward throws an exception, the matchmaker will be cleaned up.
				// A user that starts a private lobby queues automatically in this method if their request includes a deckId
				if (request.getDeckId() != null) {
					queued = Matchmaking.enqueue(new MatchmakingRequest()
							.setQueueId(customQueueId)
							.withUserId(user.getId())
							.withDeckId(request.getDeckId()));
				}
			}

			mongo().insert(INVITES, mapFrom(invite));

			sendInvite(invite);

			return new InviteResponse()
					.invite(invite);
		} catch (RuntimeException any) {
			// Make sure to clean up resources
			if (matchmaker != null) {
				matchmaker.close((ignored) -> {
				});
			}

			// If we queued, make sure to dequeue.
			if (queued) {
				Matchmaking.dequeue(new UserId(user.getId()));
			}

			// Rethrowing
			throw any;
		}
	}

	/**
	 * Sends the invite to both users on it across the {@link Connection}.
	 * <p>
	 * Mutates the incoming invite if it's {@link StatusEnum#UNDELIVERED}.
	 *
	 * @param invite
	 * @throws SuspendExecution
	 */
	static void sendInvite(@NotNull Invite invite) throws SuspendExecution {
		// Notify both users of the new invite, but only wait to see if the recipient is around to actually receive it right
		// now. We'll update the record immediately and only insert it into the db with the proper status
		var toUserConnection = Connection.writeStream(invite.getToUserId());
		if (invite.getStatus() == StatusEnum.UNDELIVERED) {
			invite.status(StatusEnum.PENDING);
		}
		toUserConnection.write(
				new Envelope().added(new EnvelopeAdded().invite(invite))
		);

		// The sender can receive this invite at any time through the channel since they will receive it in their post
		// response also.
		Connection.writeStream(invite.getFromUserId()).write(
				new Envelope().added(new EnvelopeAdded().invite(invite))
		);
	}

	/**
	 * Accepts an invite.
	 * <p>
	 * <ul>
	 * <li>If it's a matchmaking invitation, the user will automatically be put into a matchmaking queue and will
	 * wait.</li>
	 * <li>If it's a friend invitation, the user will do a mutual friend adding.</li>
	 * </ul>
	 *
	 * @param inviteId  The invite to accept
	 * @param request   The request
	 * @param recipient The user executing the request
	 * @return An updated invite and potentially information about the new friend or game.
	 * @throws SuspendExecution
	 * @throws InterruptedException
	 */
	static @NotNull
	AcceptInviteResponse accept(@NotNull InviteId inviteId, @NotNull AcceptInviteRequest request, @NotNull UserRecord recipient) throws SuspendExecution, InterruptedException {
		var invite = mongo().findOne(INVITES, json("_id", inviteId.toString()), Invite.class);
		if (invite == null) {
			throw new NullPointerException(String.format("Invite not found: %s", inviteId));
		}
		if (!invite.getToUserId().equals(recipient.getId())) {
			throw new SecurityException("You did not have access to change this invite.");
		}
		if (request.getMatch() != null) {
			if (request.getMatch().getDeckId() == null) {
				throw new NullPointerException("No deckId specified.");
			}
			if (request.getMatch().getQueueId() != null && !request.getMatch().getQueueId().equals(invite.getQueueId())) {
				throw new IllegalArgumentException("Invalid queueId specified. Leave this field empty or use the invite's queueId");
			}
		}

		switch (invite.getStatus()) {
			case PENDING:
			case UNDELIVERED:
				invite.setStatus(StatusEnum.ACCEPTED);
				break;
			case ACCEPTED:
				throw new IllegalStateException("The invite was already accepted.");
			case TIMEOUT:
				throw new IllegalStateException("The invite timed out.");
			case REJECTED:
				throw new IllegalStateException("The invite was rejected.");
			case CANCELLED:
				throw new IllegalStateException("The invite was canceled.");
		}

		// Check that the opponent is still in the queue
		if (invite.getQueueId() != null) {
			if (!Objects.equals(Matchmaking.getUsersInQueues().get(invite.getFromUserId()), (invite.getQueueId()))) {
				throw new IllegalStateException("Opponent no longer in queue.");
			}
		}

		var res = new AcceptInviteResponse();
		res.invite(invite);
		var env = new Envelope();
		if (invite.getFriendId() != null) {
			// Make them friends, regardless if they are already friends.
			res.friend(Friends.putFriend(recipient, new FriendPutRequest().friendId(invite.getFriendId())));
		}

		if (invite.getQueueId() != null) {
			try {
				// Regardless of what the client specified as its queueId, use the one from the invite
				var match = request.getMatch();
				match.queueId(invite.getQueueId());
				Matchmaking.enqueue(new MatchmakingRequest(match, recipient.getId()));
				// TODO: Wait a beat for the game to start, but actually set up a thing to wait for when it actually starts
				Strand.sleep(2000L);
				env.result(new EnvelopeResult().enqueue(new MatchmakingQueuePutResponse()));
				res.match(new MatchmakingQueuePutResponse());
			} catch (IllegalStateException alreadyInMatch) {
				// Reject the invite if the player was already in a match.
				invite.setStatus(StatusEnum.REJECTED);
				// This does the updating
				deleteInvite(new InviteId(invite.getId()), new UserId(recipient.getId()));
				return res;
			}
		}

		// Only actually accept the invite if the actions suceeded.
		mongo().updateCollection(INVITES, json("_id", invite.getId()), json("$set", json("status", StatusEnum.ACCEPTED.getValue())));

		env.changed(new EnvelopeChanged().invite(invite));
		var recipientConnection = Connection.writeStream(recipient.getId());
		// Notify the client they've been enqueued by accepting. This should shortly lead to the game starting.
		recipientConnection.write(env);

		var senderConnection = Connection.writeStream(invite.getFromUserId());
		// Notify the client they've been enqueued by accepting. This should shortly lead to the game starting.
		senderConnection.write(new Envelope().changed(new EnvelopeChanged().invite(invite)));

		// TODO: Only yield to the caller when the game has started?

		return res;
	}

	/**
	 * Deletes the invite only if it is pending or undelivered. Other kinds of invites cannot be deleted.
	 * <p>
	 * This will cancel the invite, or reject it if the recipient is deleting it.
	 *
	 * @param inviteId
	 * @param userId
	 * @return
	 * @throws SuspendExecution
	 */
	static @NotNull
	InviteResponse deleteInvite(@NotNull InviteId inviteId, @NotNull UserId userId) throws SuspendExecution {
		var invite = mongo().findOne(INVITES, json("_id", inviteId.toString()), Invite.class);

		if (invite == null) {
			throw new NullPointerException(String.format("Invite not found: %s", inviteId));
		}

		var isSender = invite.getFromUserId().equals(userId.toString());
		var isRecipient = invite.getToUserId().equals(userId.toString());
		if (!isSender && !isRecipient) {
			throw new SecurityException("You did not have access to change this invite.");
		}

		var status = isSender ? StatusEnum.CANCELLED : StatusEnum.REJECTED;
		switch (invite.getStatus()) {
			case PENDING:
			case UNDELIVERED:
				invite.setStatus(status);
				break;
			case ACCEPTED:
				throw new IllegalStateException("The invite was already accepted.");
			case TIMEOUT:
				throw new IllegalStateException("The invite timed out.");
			case REJECTED:
				throw new IllegalStateException("The invite was rejected.");
			case CANCELLED:
				throw new IllegalStateException("The invite was already canceled.");
		}

		mongo().updateCollection(INVITES, json("_id", invite.getId()), json("$set", json("status", status)));

		var env = new Envelope().changed(new EnvelopeChanged().invite(invite));
		var recipientConnection = Connection.writeStream(invite.getToUserId());
		// Notify the client they've been enqueued by accepting. This should shortly lead to the game starting.
		recipientConnection.write(env);

		var senderConnection = Connection.writeStream(invite.getFromUserId());
		// Notify the client they've been enqueued by accepting. This should shortly lead to the game starting.
		senderConnection.write(env);

		// Dequeue the sender if this was a matchmaking invite
		if (invite.getQueueId() != null) {
			Matchmaking.dequeue(new UserId(invite.getFromUserId()));
		}
		return new InviteResponse().invite(invite);
	}
}
