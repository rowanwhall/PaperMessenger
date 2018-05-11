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
 * Model definition for Friend.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the messaging. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Friend extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String pending;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean pendingMe;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean pendingThem;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String rejected;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String sentByMe;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private User user;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPending() {
    return pending;
  }

  /**
   * @param pending pending or {@code null} for none
   */
  public Friend setPending(java.lang.String pending) {
    this.pending = pending;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getPendingMe() {
    return pendingMe;
  }

  /**
   * @param pendingMe pendingMe or {@code null} for none
   */
  public Friend setPendingMe(java.lang.Boolean pendingMe) {
    this.pendingMe = pendingMe;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getPendingThem() {
    return pendingThem;
  }

  /**
   * @param pendingThem pendingThem or {@code null} for none
   */
  public Friend setPendingThem(java.lang.Boolean pendingThem) {
    this.pendingThem = pendingThem;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getRejected() {
    return rejected;
  }

  /**
   * @param rejected rejected or {@code null} for none
   */
  public Friend setRejected(java.lang.String rejected) {
    this.rejected = rejected;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSentByMe() {
    return sentByMe;
  }

  /**
   * @param sentByMe sentByMe or {@code null} for none
   */
  public Friend setSentByMe(java.lang.String sentByMe) {
    this.sentByMe = sentByMe;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user user or {@code null} for none
   */
  public Friend setUser(User user) {
    this.user = user;
    return this;
  }

  @Override
  public Friend set(String fieldName, Object value) {
    return (Friend) super.set(fieldName, value);
  }

  @Override
  public Friend clone() {
    return (Friend) super.clone();
  }

}
