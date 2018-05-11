/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2016-10-17 16:43:55 UTC)
 * on 2016-11-20 at 07:23:05 UTC 
 * Modify at your own risk.
 */

package com.personal.rowan.paperforspotify.backend.messaging.model;

/**
 * Model definition for Chat.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the messaging. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Chat extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String chatArtistName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String chatSongImageUrl;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String chatSongName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String chatSongUri;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String chatSongUrl;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String creatorId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String creatorName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String hasRead;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String lastMessage;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long updatedTimestamp;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getChatArtistName() {
    return chatArtistName;
  }

  /**
   * @param chatArtistName chatArtistName or {@code null} for none
   */
  public Chat setChatArtistName(java.lang.String chatArtistName) {
    this.chatArtistName = chatArtistName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getChatSongImageUrl() {
    return chatSongImageUrl;
  }

  /**
   * @param chatSongImageUrl chatSongImageUrl or {@code null} for none
   */
  public Chat setChatSongImageUrl(java.lang.String chatSongImageUrl) {
    this.chatSongImageUrl = chatSongImageUrl;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getChatSongName() {
    return chatSongName;
  }

  /**
   * @param chatSongName chatSongName or {@code null} for none
   */
  public Chat setChatSongName(java.lang.String chatSongName) {
    this.chatSongName = chatSongName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getChatSongUri() {
    return chatSongUri;
  }

  /**
   * @param chatSongUri chatSongUri or {@code null} for none
   */
  public Chat setChatSongUri(java.lang.String chatSongUri) {
    this.chatSongUri = chatSongUri;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getChatSongUrl() {
    return chatSongUrl;
  }

  /**
   * @param chatSongUrl chatSongUrl or {@code null} for none
   */
  public Chat setChatSongUrl(java.lang.String chatSongUrl) {
    this.chatSongUrl = chatSongUrl;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCreatorId() {
    return creatorId;
  }

  /**
   * @param creatorId creatorId or {@code null} for none
   */
  public Chat setCreatorId(java.lang.String creatorId) {
    this.creatorId = creatorId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCreatorName() {
    return creatorName;
  }

  /**
   * @param creatorName creatorName or {@code null} for none
   */
  public Chat setCreatorName(java.lang.String creatorName) {
    this.creatorName = creatorName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getHasRead() {
    return hasRead;
  }

  /**
   * @param hasRead hasRead or {@code null} for none
   */
  public Chat setHasRead(java.lang.String hasRead) {
    this.hasRead = hasRead;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Chat setId(java.lang.String id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLastMessage() {
    return lastMessage;
  }

  /**
   * @param lastMessage lastMessage or {@code null} for none
   */
  public Chat setLastMessage(java.lang.String lastMessage) {
    this.lastMessage = lastMessage;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * @param name name or {@code null} for none
   */
  public Chat setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getUpdatedTimestamp() {
    return updatedTimestamp;
  }

  /**
   * @param updatedTimestamp updatedTimestamp or {@code null} for none
   */
  public Chat setUpdatedTimestamp(java.lang.Long updatedTimestamp) {
    this.updatedTimestamp = updatedTimestamp;
    return this;
  }

  @Override
  public Chat set(String fieldName, Object value) {
    return (Chat) super.set(fieldName, value);
  }

  @Override
  public Chat clone() {
    return (Chat) super.clone();
  }

}