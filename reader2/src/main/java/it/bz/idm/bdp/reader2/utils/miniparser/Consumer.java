package it.bz.idm.bdp.reader2.utils.miniparser;

public interface Consumer extends SimpleConsumer {
	boolean before(Token t);
	boolean after(Token t);
}
