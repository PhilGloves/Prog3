package unito.prog3.network;

import java.io.Serializable;

public enum Connection implements Serializable {

  LOGIN,
  REGISTRATION,

  INBOX,
  SENTBOX,

  SEND,
  DELETE,
  DELETEREPLY,
  READ,
  REPLY,


  OK,
  FAIL

}
