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
 * Model definition for ReadReceipt.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the messaging. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ReadReceipt extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String chatId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String chatMessageId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String message;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getChatId() {
    return chatId;
  }

  /**
   * @param chatId chatId or {@code null} for none
   */
  public ReadReceipt setChatId(java.lang.String chatId) {
    this.chatId = chatId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getChatMessageId() {
    return chatMessageId;
  }

  /**
   * @param chatMessageId chatMessageId or {@code null} for none
   */
  public ReadReceipt setChatMessageId(java.lang.String chatMessageId) {
    this.chatMessageId = chatMessageId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMessage() {
    return message;
  }

  /**
   * @param message message or {@code null} for none
   */
  public ReadReceipt setMessage(java.lang.String message) {
    this.message = message;
    return this;
  }

  @Override
  public ReadReceipt set(String fieldName, Object value) {
    return (ReadReceipt) super.set(fieldName, value);
  }

  @Override
  public ReadReceipt clone() {
    return (ReadReceipt) super.clone();
  }

}