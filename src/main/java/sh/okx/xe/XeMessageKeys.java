package sh.okx.xe;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

public enum XeMessageKeys implements MessageKeyProvider {
  ADD_SUCCESS,
  ADD_RECEIVE,
  TAKE_SUCCESS,
  TAKE_RECEIVE,
  SET_SUCCESS,
  SET_RECEIVE,
  BAL_SPECIFIC_SELF,
  BAL_SPECIFIC_OTHER,
  BAL_ALL_HEADER_SELF,
  BAL_ALL_HEADER_OTHER,
  BAL_ALL,
  ;

  private final MessageKey key = MessageKey.of("xe." + this.name().toLowerCase());

  @Override
  public MessageKey getMessageKey() {
    return key;
  }
}
