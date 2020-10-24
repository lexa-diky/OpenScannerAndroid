package com.skosc.oscan

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import java.lang.Runnable
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class OpenScannerSimpleCoroutineExecutor(
    lifecycleOwner: LifecycleOwner,
    scope: CoroutineScope = GlobalScope,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    name: String = DEFAULT_COROUTINE_NAME
) : Executor, LifecycleObserver {

    private val channel = ConflatedBroadcastChannel<Runnable>()

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    init {
        scope.launch(dispatcher + CoroutineName(name)) {
            channel.asFlow()
                .collect { runnable ->
                    runnable.run()
                }
        }
    }

    override fun execute(runnable: Runnable) {
        if (!channel.isClosedForSend) {
            channel.offer(runnable)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        channel.close()
    }

    companion object {

        private const val DEFAULT_COROUTINE_NAME = "OpenScannerSimpleCoroutineExecutor"
    }
}
