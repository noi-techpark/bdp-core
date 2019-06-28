package it.bz.idm.bdp.reader2.utils.miniparser;

public interface SimpleConsumer {
	boolean middle(Token t);

	default String getDescription(Object... data) {
		return "";
	}
}
