package com.wp.app.resource.basic.net;

public interface DataListener {
	void dataStart();
	
	void dataStop();
	
	void dataOther(StatusInfo statusInfo);
}
