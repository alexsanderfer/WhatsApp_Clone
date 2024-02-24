/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/24. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.alexsander.whatsappclone.R
import com.alexsander.whatsappclone.databinding.ActivityMainBinding
import com.alexsander.whatsappclone.ui.login.LoginActivity
import com.alexsander.whatsappclone.ui.login.profile.ProfileActivity
import com.alexsander.whatsappclone.utils.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val auth by lazy { FirebaseAuth.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeToolbar()
        initializeNavTabs()
        initializeClickEvents()
    }

    private fun initializeNavTabs() {
        val tabLayout = binding.mainTabLayout
        val viewPager = binding.mainViewPager

        val tabs = listOf("Conversas", "Contatos")
        viewPager.adapter = ViewPagerAdapter(tabs, supportFragmentManager, lifecycle)

        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun initializeClickEvents() {
        // TODO:
    }

    private fun initializeToolbar() {
        setSupportActionBar(binding.includeMainToolbar.mMainToolbar)
        supportActionBar?.apply {
            title = getString(R.string.app_name)
        }
        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.item_profile -> {
                            startActivity(Intent(applicationContext, ProfileActivity::class.java))
                        }

                        R.id.item_logout -> {
                            singOutUser()
                        }
                    }
                    return true
                }
            }
        )
    }

    private fun singOutUser() {
        AlertDialog.Builder(this)
            .setTitle("Fazer logout")
            .setMessage("Deseja fazer logout??")
            .setNegativeButton("Não") { dialog, i ->
                dialog.cancel()
            }
            .setPositiveButton("Sim") { _, i ->
                auth.signOut()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            .create()
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
