package org.kmm.airpurifier.di

import org.kmm.airpurifier.dependencies.HomeViewModel
import org.kmm.airpurifier.dependencies.ScannerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    viewModel { ScannerViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
}