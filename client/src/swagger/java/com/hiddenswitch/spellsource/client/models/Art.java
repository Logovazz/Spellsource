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
import com.hiddenswitch.spellsource.client.models.Color;
import com.hiddenswitch.spellsource.client.models.Font;
import com.hiddenswitch.spellsource.client.models.Sprite;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Contains information the client needs for an art asset. 
 */
@ApiModel(description = "Contains information the client needs for an art asset. ")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class Art implements Serializable {

  @JsonProperty("sprite")
  private Sprite sprite = null;

  @JsonProperty("spriteShadow")
  private Sprite spriteShadow = null;

  @JsonProperty("primary")
  private Color primary = null;

  @JsonProperty("secondary")
  private Color secondary = null;

  @JsonProperty("highlight")
  private Color highlight = null;

  @JsonProperty("shadow")
  private Color shadow = null;

  @JsonProperty("body")
  private Font body = null;

  public Art sprite(Sprite sprite) {
    this.sprite = sprite;
    return this;
  }

   /**
   * Get sprite
   * @return sprite
  **/
  @ApiModelProperty(value = "")
  public Sprite getSprite() {
    return sprite;
  }

  public void setSprite(Sprite sprite) {
    this.sprite = sprite;
  }

  public Art spriteShadow(Sprite spriteShadow) {
    this.spriteShadow = spriteShadow;
    return this;
  }

   /**
   * Get spriteShadow
   * @return spriteShadow
  **/
  @ApiModelProperty(value = "")
  public Sprite getSpriteShadow() {
    return spriteShadow;
  }

  public void setSpriteShadow(Sprite spriteShadow) {
    this.spriteShadow = spriteShadow;
  }

  public Art primary(Color primary) {
    this.primary = primary;
    return this;
  }

   /**
   * Get primary
   * @return primary
  **/
  @ApiModelProperty(value = "")
  public Color getPrimary() {
    return primary;
  }

  public void setPrimary(Color primary) {
    this.primary = primary;
  }

  public Art secondary(Color secondary) {
    this.secondary = secondary;
    return this;
  }

   /**
   * Get secondary
   * @return secondary
  **/
  @ApiModelProperty(value = "")
  public Color getSecondary() {
    return secondary;
  }

  public void setSecondary(Color secondary) {
    this.secondary = secondary;
  }

  public Art highlight(Color highlight) {
    this.highlight = highlight;
    return this;
  }

   /**
   * Get highlight
   * @return highlight
  **/
  @ApiModelProperty(value = "")
  public Color getHighlight() {
    return highlight;
  }

  public void setHighlight(Color highlight) {
    this.highlight = highlight;
  }

  public Art shadow(Color shadow) {
    this.shadow = shadow;
    return this;
  }

   /**
   * Get shadow
   * @return shadow
  **/
  @ApiModelProperty(value = "")
  public Color getShadow() {
    return shadow;
  }

  public void setShadow(Color shadow) {
    this.shadow = shadow;
  }

  public Art body(Font body) {
    this.body = body;
    return this;
  }

   /**
   * Get body
   * @return body
  **/
  @ApiModelProperty(value = "")
  public Font getBody() {
    return body;
  }

  public void setBody(Font body) {
    this.body = body;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Art art = (Art) o;
    return Objects.equals(this.sprite, art.sprite) &&
        Objects.equals(this.spriteShadow, art.spriteShadow) &&
        Objects.equals(this.primary, art.primary) &&
        Objects.equals(this.secondary, art.secondary) &&
        Objects.equals(this.highlight, art.highlight) &&
        Objects.equals(this.shadow, art.shadow) &&
        Objects.equals(this.body, art.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sprite, spriteShadow, primary, secondary, highlight, shadow, body);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Art {\n");
    
    sb.append("    sprite: ").append(toIndentedString(sprite)).append("\n");
    sb.append("    spriteShadow: ").append(toIndentedString(spriteShadow)).append("\n");
    sb.append("    primary: ").append(toIndentedString(primary)).append("\n");
    sb.append("    secondary: ").append(toIndentedString(secondary)).append("\n");
    sb.append("    highlight: ").append(toIndentedString(highlight)).append("\n");
    sb.append("    shadow: ").append(toIndentedString(shadow)).append("\n");
    sb.append("    body: ").append(toIndentedString(body)).append("\n");
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

