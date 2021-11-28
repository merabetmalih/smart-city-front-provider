package com.smartcity.provider.repository.auth

import com.smartcity.provider.di.auth.AuthScope
import com.smartcity.provider.ui.auth.state.AuthViewState
import com.smartcity.provider.util.DataState
import com.smartcity.provider.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@FlowPreview
@AuthScope
interface AuthRepository {

    fun attemptLogin(
        stateEvent: StateEvent,
        email: String,
        password: String
    ): Flow<DataState<AuthViewState>>

    fun attemptRegistration(
        stateEvent: StateEvent,
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): Flow<DataState<AuthViewState>>

    fun checkPreviousAuthUser(
        stateEvent: StateEvent
    ): Flow<DataState<AuthViewState>>

    fun saveAuthenticatedUserToPrefs(
        email: String
    )

    fun returnNoTokenFound(
        stateEvent: StateEvent
    ): DataState<AuthViewState>
}