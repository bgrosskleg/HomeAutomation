package subscribers;

import java.util.EventListener;

public interface CurrentModelSubscriber extends EventListener
{
	public void currentModelChanged();
}
