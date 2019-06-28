package it.bz.idm.bdp.reader2.utils.miniparser;

public interface ConsumerExtended extends Consumer {
	boolean before(Token t);
	boolean after(Token t);
}
