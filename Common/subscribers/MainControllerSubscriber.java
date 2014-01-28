package subscribers;

import java.util.EventListener;

public interface MainControllerSubscriber extends EventListener
{
	public void mainControllerChanged();
}

