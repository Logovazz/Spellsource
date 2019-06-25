/*
 * Hidden Switch Spellsource API
 * The Spellsource API for matchmaking, user accounts, collections management and more.  To get started, create a user account and make sure to include the entirety of the returned login token as the X-Auth-Token header. You can reuse this token, or login for a new one.  ClientToServerMessage and ServerToClientMessage are used for the realtime game state and actions two-way websocket interface for actually playing a game. Envelope is used for the realtime API services.  For the routes that correspond to the paths in this file, visit the Gateway.java file in the Spellsource-Server GitHub repository located in this definition file. 
 *
 * OpenAPI spec version: 3.0.5
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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Adds the specified card IDs to the deck with this command. If the player doesn&#39;t own the card IDs, the current Spellsource inventory rules will grant the cards to the user. Duplicates are allowed. Under standard rules, the deck becomes invalid if the number of duplicates exceeds 2; or, if the hero class isn&#39;t neutral or the same as the deck&#39;s hero class. 
 */
@ApiModel(description = "Adds the specified card IDs to the deck with this command. If the player doesn't own the card IDs, the current Spellsource inventory rules will grant the cards to the user. Duplicates are allowed. Under standard rules, the deck becomes invalid if the number of duplicates exceeds 2; or, if the hero class isn't neutral or the same as the deck's hero class. ")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class DecksUpdateCommandPushCardIds implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("$each")
  private List<String> each = new ArrayList<>();

  public DecksUpdateCommandPushCardIds each(List<String> each) {
    this.each = each;
    return this;
  }

  public DecksUpdateCommandPushCardIds addEachItem(String eachItem) {
    this.each.add(eachItem);
    return this;
  }

   /**
   * The items in this array specify which card IDs should be added. 
   * @return each
  **/
  @ApiModelProperty(required = true, value = "The items in this array specify which card IDs should be added. ")
  public List<String> getEach() {
    return each;
  }

  public void setEach(List<String> each) {
    this.each = each;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DecksUpdateCommandPushCardIds decksUpdateCommandPushCardIds = (DecksUpdateCommandPushCardIds) o;
    return Objects.equals(this.each, decksUpdateCommandPushCardIds.each);
  }

  @Override
  public int hashCode() {
    return Objects.hash(each);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DecksUpdateCommandPushCardIds {\n");
    
    sb.append("    each: ").append(toIndentedString(each)).append("\n");
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

