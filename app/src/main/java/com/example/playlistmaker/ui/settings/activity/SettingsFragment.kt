package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.SettingsFragmentBinding
import com.example.playlistmaker.ui.settings.viewModel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private lateinit var settingsBinding: SettingsFragmentBinding
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        settingsBinding = SettingsFragmentBinding.inflate(inflater, container, false)
        return settingsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initClickListeners()
    }

    private fun initObservers() {
        // переключение темы
        settingsViewModel.isDarkThemeEnabled.observe(viewLifecycleOwner) { isEnabled ->
            settingsBinding.themeSwitcher.isChecked = isEnabled
        }
    }

    private fun initClickListeners() {
        settingsBinding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.switchTheme(isChecked)
        }

        // поделиться приложением
        settingsBinding.shareSettings.setOnClickListener{
            settingsViewModel.shareApp(requireContext())
        }

        // написать в поддержку
        settingsBinding.supportSettings.setOnClickListener{
            settingsViewModel.contactSupport(requireContext())
        }

        // пользовательское соглашение
        settingsBinding.userAgreementSettings.setOnClickListener {
            settingsViewModel.openUserAgreement(requireContext())
        }
    }
}