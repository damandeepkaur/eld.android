package app.bsmuniversal.com;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class RxSchedulerRule implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // Override the default AndroidSchedulers.mainThread() scheduler
                Function<Callable<Scheduler>, Scheduler> testAndroidHandler = new Function<Callable<Scheduler>, Scheduler>() {
                    @Override
                    public Scheduler apply(@NonNull Callable<Scheduler> schedulerCallable) throws Exception {
                        return Schedulers.trampoline();
                    }
                };

                // Override the default java schedulers
                Function<Scheduler, Scheduler> testJavaHandler = new Function<Scheduler, Scheduler>() {
                    @Override
                    public Scheduler apply(@NonNull Scheduler scheduler) throws Exception {
                        return Schedulers.trampoline();
                    }
                };

                RxAndroidPlugins.setInitMainThreadSchedulerHandler(testAndroidHandler);

                RxJavaPlugins.setIoSchedulerHandler(testJavaHandler);
                RxJavaPlugins.setNewThreadSchedulerHandler(testJavaHandler);
                RxJavaPlugins.setComputationSchedulerHandler(testJavaHandler);

                try {
                    base.evaluate();
                } finally {
                    RxAndroidPlugins.reset();
                    RxJavaPlugins.reset();
                }
            }
        };
    }
}
