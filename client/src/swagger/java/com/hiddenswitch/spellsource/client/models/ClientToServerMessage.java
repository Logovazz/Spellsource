/*
 * Hidden Switch Spellsource API
 * The Spellsource API for matchmaking, user accounts, collections management and more.  To get started, create a user account and make sure to include the entirety of the returned login token as the X-Auth-Token header. You can reuse this token, or login for a new one.  ClientToServerMessage and ServerToClientMessage are used for the realtime game state and actions two-way websocket interface for actually playing a game. Envelope is used for the realtime API services.  For the routes that correspond to the paths in this file, visit the Gateway.java file in the Spellsource-Server GitHub repository located in this definition file. 
 *
 * OpenAPI spec version: 4.0.1
 * Contact: ben@hiddenswitch.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.hiddenswitch.spellsource.client.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hiddenswitch.spellsource.client.models.ClientToServerMessageFirstMessage;
import com.hiddenswitch.spellsource.client.models.Emote;
import com.hiddenswitch.spellsource.client.models.MessageType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * ClientToServerMessage
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class ClientToServerMessage implements Serializable {

  @JsonProperty("messageType")
  private MessageType messageType = null;

  @JsonProperty("repliesTo")
  private String repliesTo = null;

  @JsonProperty("firstMessage")
  private ClientToServerMessageFirstMessage firstMessage = null;

  @JsonProperty("actionIndex")
  private Integer actionIndex = null;

  @JsonProperty("discardedCardIndices")
  private List<Integer> discardedCardIndices = null;

  @JsonProperty("emote")
  private Emote emote = null;

  @JsonProperty("entityTouch")
  private Integer entityTouch = null;

  @JsonProperty("entityUntouch")
  private Integer entityUntouch = null;

  public ClientToServerMessage messageType(MessageType messageType) {
    this.messageType = messageType;
    return this;
  }

   /**
   * Get messageType
   * @return messageType
  **/
  @ApiModelProperty(value = "")
  public MessageType getMessageType() {
    return messageType;
  }

  public void setMessageType(MessageType messageType) {
    this.messageType = messageType;
  }

  public ClientToServerMessage repliesTo(String repliesTo) {
    this.repliesTo = repliesTo;
    return this;
  }

   /**
   * The ID of the server message this client message is replying to. 
   * @return repliesTo
  **/
  @ApiModelProperty(value = "The ID of the server message this client message is replying to. ")
  public String getRepliesTo() {
    return repliesTo;
  }

  public void setRepliesTo(String repliesTo) {
    this.repliesTo = repliesTo;
  }

  public ClientToServerMessage firstMessage(ClientToServerMessageFirstMessage firstMessage) {
    this.firstMessage = firstMessage;
    return this;
  }

   /**
   * Get firstMessage
   * @return firstMessage
  **/
  @ApiModelProperty(value = "")
  public ClientToServerMessageFirstMessage getFirstMessage() {
    return firstMessage;
  }

  public void setFirstMessage(ClientToServerMessageFirstMessage firstMessage) {
    this.firstMessage = firstMessage;
  }

  public ClientToServerMessage actionIndex(Integer actionIndex) {
    this.actionIndex = actionIndex;
    return this;
  }

   /**
   * The index of the available actions to use. 
   * @return actionIndex
  **/
  @ApiModelProperty(value = "The index of the available actions to use. ")
  public Integer getActionIndex() {
    return actionIndex;
  }

  public void setActionIndex(Integer actionIndex) {
    this.actionIndex = actionIndex;
  }

  public ClientToServerMessage discardedCardIndices(List<Integer> discardedCardIndices) {
    this.discardedCardIndices = discardedCardIndices;
    return this;
  }

  public ClientToServerMessage addDiscardedCardIndicesItem(Integer discardedCardIndicesItem) {
    if (this.discardedCardIndices == null) {
      this.discardedCardIndices = new ArrayList<>();
    }
    this.discardedCardIndices.add(discardedCardIndicesItem);
    return this;
  }

   /**
   * The indices of cards to discard in a mulligan.
   * @return discardedCardIndices
  **/
  @ApiModelProperty(value = "The indices of cards to discard in a mulligan.")
  public List<Integer> getDiscardedCardIndices() {
    return discardedCardIndices;
  }

  public void setDiscardedCardIndices(List<Integer> discardedCardIndices) {
    this.discardedCardIndices = discardedCardIndices;
  }

  public ClientToServerMessage emote(Emote emote) {
    this.emote = emote;
    return this;
  }

   /**
   * Get emote
   * @return emote
  **/
  @ApiModelProperty(value = "")
  public Emote getEmote() {
    return emote;
  }

  public void setEmote(Emote emote) {
    this.emote = emote;
  }

  public ClientToServerMessage entityTouch(Integer entityTouch) {
    this.entityTouch = entityTouch;
    return this;
  }

   /**
   * When specified with an entity ID, indicates the client is \&quot;touching\&quot; this entity. 
   * @return entityTouch
  **/
  @ApiModelProperty(value = "When specified with an entity ID, indicates the client is \"touching\" this entity. ")
  public Integer getEntityTouch() {
    return entityTouch;
  }

  public void setEntityTouch(Integer entityTouch) {
    this.entityTouch = entityTouch;
  }

  public ClientToServerMessage entityUntouch(Integer entityUntouch) {
    this.entityUntouch = entityUntouch;
    return this;
  }

   /**
   * When specified with an entity ID, indicates the client is no longer touching the specified entity. 
   * @return entityUntouch
  **/
  @ApiModelProperty(value = "When specified with an entity ID, indicates the client is no longer touching the specified entity. ")
  public Integer getEntityUntouch() {
    return entityUntouch;
  }

  public void setEntityUntouch(Integer entityUntouch) {
    this.entityUntouch = entityUntouch;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClientToServerMessage clientToServerMessage = (ClientToServerMessage) o;
    return Objects.equals(this.messageType, clientToServerMessage.messageType) &&
        Objects.equals(this.repliesTo, clientToServerMessage.repliesTo) &&
        Objects.equals(this.firstMessage, clientToServerMessage.firstMessage) &&
        Objects.equals(this.actionIndex, clientToServerMessage.actionIndex) &&
        Objects.equals(this.discardedCardIndices, clientToServerMessage.discardedCardIndices) &&
        Objects.equals(this.emote, clientToServerMessage.emote) &&
        Objects.equals(this.entityTouch, clientToServerMessage.entityTouch) &&
        Objects.equals(this.entityUntouch, clientToServerMessage.entityUntouch);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageType, repliesTo, firstMessage, actionIndex, discardedCardIndices, emote, entityTouch, entityUntouch);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ClientToServerMessage {\n");
    
    sb.append("    messageType: ").append(toIndentedString(messageType)).append("\n");
    sb.append("    repliesTo: ").append(toIndentedString(repliesTo)).append("\n");
    sb.append("    firstMessage: ").append(toIndentedString(firstMessage)).append("\n");
    sb.append("    actionIndex: ").append(toIndentedString(actionIndex)).append("\n");
    sb.append("    discardedCardIndices: ").append(toIndentedString(discardedCardIndices)).append("\n");
    sb.append("    emote: ").append(toIndentedString(emote)).append("\n");
    sb.append("    entityTouch: ").append(toIndentedString(entityTouch)).append("\n");
    sb.append("    entityUntouch: ").append(toIndentedString(entityUntouch)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

