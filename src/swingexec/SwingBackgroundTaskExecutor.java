package swingexec;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.SwingWorker;

/**
 * Based on: https://dzone.com/articles/simple-executor-pattern
 */
public class SwingBackgroundTaskExecutor {
	private static final SwingBackgroundTaskExecutor instance = new SwingBackgroundTaskExecutor();

	private SwingBackgroundTaskExecutor() {
	}

	public static SwingBackgroundTaskExecutor getInstance() {
		return instance;
	}

	static class BackgroundSwingWorker<T, V> extends SwingWorker<T, V> {

		private final SwingBackgroundTask<T, V> backgroundTask;
		private final SwingUiRenderer<T, V> uiRenderer;

		public BackgroundSwingWorker(SwingBackgroundTask<T, V> backgroundTask,
				SwingUiRenderer<T, V> uiRenderer) {

			super();
			this.backgroundTask = backgroundTask;
			this.uiRenderer = uiRenderer;
		}

		@Override
		protected T doInBackground() throws Exception {
			Callable<T> callable = new Callable<T>() {
				public T call() throws Exception {
					return BackgroundSwingWorker.this.backgroundTask
							.doInBackground();
				}
			};

			ExecutorService executorService = Executors.newFixedThreadPool(1);
			Future<T> wrappedFuture = executorService.submit(callable);

			while (!wrappedFuture.isDone() && !wrappedFuture.isCancelled()) {
				// cancel the wrapped future if original task was cancelled
				if (isCancelled()) {
					wrappedFuture.cancel(true);
				}
				V result = this.backgroundTask.getNextResultChunk();
				if (result != null) {
					this.publish(result);
				}
			}
			this.uiRenderer.done(wrappedFuture.get());
			return wrappedFuture.get();
		}

		@Override
		protected void process(List<V> intermediateResults) {

		}

		public Future<T> getFuture() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public <V, T> Future<T> execute(SwingBackgroundTask<T, V> backgroundTask,
			SwingUiRenderer<T, V> renderer) {
		BackgroundSwingWorker<T, V> swingWorker = new BackgroundSwingWorker<T, V>(
				backgroundTask, renderer);
		swingWorker.execute();
		return swingWorker.getFuture();
	}
}
