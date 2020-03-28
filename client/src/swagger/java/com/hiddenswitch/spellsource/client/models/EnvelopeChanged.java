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
import com.hiddenswitch.spellsource.client.models.EditableCard;
import com.hiddenswitch.spellsource.client.models.Friend;
import com.hiddenswitch.spellsource.client.models.Invite;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Indicates that a record&#39;s fields have changed. 
 */
@ApiModel(description = "Indicates that a record's fields have changed. ")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class EnvelopeChanged implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("friend")
  private Friend friend = null;

  @JsonProperty("invite")
  private Invite invite = null;

  @JsonProperty("editableCard")
  private EditableCard editableCard = null;

  public EnvelopeChanged friend(Friend friend) {
    this.friend = friend;
    return this;
  }

   /**
   * Get friend
   * @return friend
  **/
  @ApiModelProperty(value = "")
  public Friend getFriend() {
    return friend;
  }

  public void setFriend(Friend friend) {
    this.friend = friend;
  }

  public EnvelopeChanged invite(Invite invite) {
    this.invite = invite;
    return this;
  }

   /**
   * Get invite
   * @return invite
  **/
  @ApiModelProperty(value = "")
  public Invite getInvite() {
    return invite;
  }

  public void setInvite(Invite invite) {
    this.invite = invite;
  }

  public EnvelopeChanged editableCard(EditableCard editableCard) {
    this.editableCard = editableCard;
    return this;
  }

   /**
   * Get editableCard
   * @return editableCard
  **/
  @ApiModelProperty(value = "")
  public EditableCard getEditableCard() {
    return editableCard;
  }

  public void setEditableCard(EditableCard editableCard) {
    this.editableCard = editableCard;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EnvelopeChanged envelopeChanged = (EnvelopeChanged) o;
    return Objects.equals(this.friend, envelopeChanged.friend) &&
        Objects.equals(this.invite, envelopeChanged.invite) &&
        Objects.equals(this.editableCard, envelopeChanged.editableCard);
  }

  @Override
  public int hashCode() {
    return Objects.hash(friend, invite, editableCard);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EnvelopeChanged {\n");
    
    sb.append("    friend: ").append(toIndentedString(friend)).append("\n");
    sb.append("    invite: ").append(toIndentedString(invite)).append("\n");
    sb.append("    editableCard: ").append(toIndentedString(editableCard)).append("\n");
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
