package unito.prog3.network;

import java.io.Serializable;

public enum Connection implements Serializable {
  DELETEREPLY,
  DELETE,
  FAIL,
  INBOX,
  LOGIN,
  OK,
  READ,
  REPLY,
  SEND,
  SENTBOX
}

