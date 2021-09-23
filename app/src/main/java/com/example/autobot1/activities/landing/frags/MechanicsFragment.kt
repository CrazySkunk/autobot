package com.example.autobot1.activities.landing.frags

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autobot1.R
import com.example.autobot1.activities.landing.viewmodels.MechanicsViewModel
import com.example.autobot1.activities.mechanics.models.Bookings
import com.example.autobot1.adapters.MechanicAdapter
import com.example.autobot1.databinding.FragmentMechanicsBinding
import com.example.autobot1.models.Notification
import com.example.autobot1.models.User
import com.example.autobot1.notification.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val TAG = "MechanicsFragment"

class MechanicsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMechanicsBinding
    lateinit var viewModel: MechanicsViewModel
    lateinit var apiService: APIService
    lateinit var me: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MechanicsViewModel::class.java)
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMechanicsBinding.inflate(inflater, container, false)
        getName(FirebaseAuth.getInstance().uid.toString())
        return binding.root
    }

    fun getName(uid: String) {
        FirebaseDatabase.getInstance().getReference("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val user = it.getValue(User::class.java)
                        if (user != null) {
                            if (user.uid == uid) {
                                me = user
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: -> ${error.message}")
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mechanics.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                binding.noMechsIv.visibility = VISIBLE
                binding.noMechsYetTv.visibility = VISIBLE
            } else {
                val mAdapter = MechanicAdapter(it)
                binding.mechanicsRecycler.apply {
                    clipToPadding = false
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = mAdapter
                    mAdapter.setOnItemClickListener(object : MechanicAdapter.OnItemClick {
                        override fun onItemClick(position: Int) {
                            FirebaseDatabase.getInstance()
                                .getReference("notification/${it[position].uid}/${FirebaseAuth.getInstance().uid}")
                                .setValue(
                                    Bookings(
                                        me.uid,
                                        me.name,
                                        me.imageUrl,
                                        it[position].uid,
                                        "",
                                        "",
                                        false
                                    )
                                )
                                .addOnCompleteListener { task ->
                                    if (task.isComplete && task.isSuccessful) {
                                        val data = Data(
                                            FirebaseAuth.getInstance().uid,
                                            "I need help....",
                                            "Autobot",
                                            it[position].uid,
                                            R.drawable.ic_launcher_foreground
                                        )
                                        val sender = Sender(data, it[position].deviceToken)
                                        apiService.sendNotification(sender)
                                            .enqueue(object : Callback<MyResponse> {
                                                override fun onResponse(
                                                    call: Call<MyResponse>,
                                                    response: Response<MyResponse>
                                                ) {
                                                    if (response.isSuccessful || response.code() == 200) {
                                                        if (response.body()!!.success != 1) {
                                                            Snackbar.make(
                                                                binding.root,
                                                                "Something went wrong try again...",
                                                                Snackbar.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<MyResponse>,
                                                    t: Throwable
                                                ) {
                                                    Log.e(TAG, "onFailure: -> ${t.message}")
                                                }

                                            })
                                    }
                                }

                        }
                    })
                }
            }
        })
    }

    fun go(geocode: String) {
        //d - driving w - walking b - bicycle l - two less vehicles
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$geocode&mode=l"))
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Snackbar.make(binding.root, "You do not have google maps", Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MechanicsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}