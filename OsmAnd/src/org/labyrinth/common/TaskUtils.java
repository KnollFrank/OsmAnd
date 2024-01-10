package org.labyrinth.common;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class TaskUtils {

    public static <TResult> TResult await(Task<TResult> task) {
        try {
            return Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean awaitForSuccess(final Task<?> task) {
        try {
            Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }

        return task.getException() == null;
    }

    public static <TResult> TResult synchronize(final Consumer<Consumer<TResult>> asyncTask) {
        final TaskCompletionSource<TResult> taskCompletionSource = new TaskCompletionSource<>();
        asyncTask.accept(taskCompletionSource::setResult);
        return await(taskCompletionSource.getTask());
    }
}
