package org.kmm.airpurifier.di

import org.kmm.airpurifier.data.repository.DeviceRepositoryImp
import org.kmm.airpurifier.domain.repository.DeviceRepository
import org.kmm.airpurifier.presentation.ui.viewmodel.HomeViewModel
import org.kmm.airpurifier.presentation.ui.viewmodel.ScannerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single<DeviceRepository> { DeviceRepositoryImp(get()) }
    viewModel { ScannerViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
}