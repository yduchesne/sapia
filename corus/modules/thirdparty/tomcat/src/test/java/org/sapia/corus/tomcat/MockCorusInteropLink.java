package org.sapia.corus.tomcat;

import java.util.ArrayList;
import java.util.List;

import org.sapia.corus.interop.Status;
import org.sapia.corus.interop.api.Implementation;
import org.sapia.corus.interop.api.ShutdownListener;
import org.sapia.corus.interop.api.StatusRequestListener;

public class MockCorusInteropLink implements Implementation {

	private List<ShutdownListener> _shutdownListeners = new ArrayList<ShutdownListener>();
	private List<StatusRequestListener> _statusListeners = new ArrayList<StatusRequestListener>();
	
	@Override
	public void addShutdownListener(ShutdownListener aListener) {
		_shutdownListeners.add(aListener);
	}

	@Override
	public void addStatusRequestListener(StatusRequestListener arg0) {
		_statusListeners.add(arg0);
	}

	@Override
	public String getCorusHost() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getCorusPid() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getCorusPort() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getDistributionDir() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getDistributionName() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getDistributionVersion() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public void restart() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void shutdown() {
		while (!_shutdownListeners.isEmpty()) {
			ShutdownListener listener = _shutdownListeners.remove(0);
			try {
				listener.onShutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void status() {
		for (StatusRequestListener listener: _statusListeners) {
			Status s = new Status();
			listener.onStatus(s);
			System.out.println(s);
		}
	}
	
}
