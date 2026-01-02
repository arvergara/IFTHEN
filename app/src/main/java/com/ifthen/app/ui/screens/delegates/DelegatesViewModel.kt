package com.ifthen.app.ui.screens.delegates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifthen.app.data.repository.DelegateRepository
import com.ifthen.app.domain.model.Delegate
import com.ifthen.app.domain.model.DelegateChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DelegatesUiState(
    val delegates: List<Delegate> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val editingDelegate: Delegate? = null
)

@HiltViewModel
class DelegatesViewModel @Inject constructor(
    private val delegateRepository: DelegateRepository
) : ViewModel() {

    private val _showAddDialog = MutableStateFlow(false)
    private val _editingDelegate = MutableStateFlow<Delegate?>(null)

    val uiState: StateFlow<DelegatesUiState> = combine(
        delegateRepository.getAllDelegatesFlow(),
        _showAddDialog,
        _editingDelegate
    ) { delegates, showDialog, editing ->
        DelegatesUiState(
            delegates = delegates,
            isLoading = false,
            showAddDialog = showDialog,
            editingDelegate = editing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DelegatesUiState()
    )

    fun showAddDialog() {
        _showAddDialog.value = true
        _editingDelegate.value = null
    }

    fun showEditDialog(delegate: Delegate) {
        _editingDelegate.value = delegate
        _showAddDialog.value = true
    }

    fun hideDialog() {
        _showAddDialog.value = false
        _editingDelegate.value = null
    }

    fun saveDelegate(name: String, channel: DelegateChannel, contact: String, areas: List<String>) {
        viewModelScope.launch {
            val editing = _editingDelegate.value
            if (editing != null) {
                delegateRepository.updateDelegate(
                    editing.copy(
                        name = name,
                        channel = channel,
                        contact = contact,
                        areas = areas
                    )
                )
            } else {
                delegateRepository.insertDelegate(
                    Delegate(
                        name = name,
                        channel = channel,
                        contact = contact,
                        areas = areas
                    )
                )
            }
            hideDialog()
        }
    }

    fun deleteDelegate(delegateId: String) {
        viewModelScope.launch {
            delegateRepository.deleteDelegateById(delegateId)
        }
    }
}
