package com.mmw.activity

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.support.v7.app.AppCompatActivity

/**
 * Created by Mathias on 31/08/2017.
 *
 */

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {

    // Temporary class until Architecture Components is final. Makes [AppCompatActivity] a
    // [LifecycleRegistryOwner].
    private val registry = LifecycleRegistry(this)
    override fun getLifecycle(): LifecycleRegistry = registry
}