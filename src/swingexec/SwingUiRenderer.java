package swingexec;

import java.util.List;

public interface SwingUiRenderer<T, V> {
	
	void  processIntermediateResults(List<V> intermediateResults, int progress);
	
	void  done(T result);

}
