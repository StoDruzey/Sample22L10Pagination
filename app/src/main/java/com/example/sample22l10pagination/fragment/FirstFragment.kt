package com.example.sample22l10pagination.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sample22l10pagination.retrofit.GithubService
import com.example.sample22l10pagination.model.PagingData
import com.example.sample22l10pagination.model.User
import com.example.sample22l10pagination.adapter.UserAdapter
import com.example.sample22l10pagination.addHorizontalSpaceDecoration
import com.example.sample22l10pagination.addPaginationScrollListener
import com.example.sample22l10pagination.databinding.FragmentFirstBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var currentRequest: Call<List<User>>? = null
    private var currentPage = 0
    private val currentUsers = mutableListOf<User>()
    private val adapter by lazy {
        UserAdapter(
            requireContext(),
            onUserClicked = {
                findNavController()
                    .navigate(
                        FirstFragmentDirections.toDetailsFragment(
                            it.avatarUrl,
                            it.login,
                            it.id
                        )
                    )
            }
        ) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFirstBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            swipeRefresh.setOnRefreshListener {
                currentPage = 0
                currentRequest?.cancel()
                currentRequest = null
                executeRequest {
                    swipeRefresh.isRefreshing = false
                }
            }
            val linearLayoutManager = LinearLayoutManager(
                view.context, LinearLayoutManager.VERTICAL, false
            )
            recyclerView.adapter = adapter
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.addHorizontalSpaceDecoration(RECYCLER_ITEM_SPACE)
            recyclerView.addPaginationScrollListener(linearLayoutManager, COUNT_TO_LOAD) {
                executeRequest()
            }

//            toolbar
//                .menu
//                .findItem(R.id.menu_search)
//                .actionView
//                .let { it as SearchView }
//                .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                    override fun onQueryTextSubmit(query: String): Boolean {
//                        return false
//                    }
//
//                    override fun onQueryTextChange(query: String): Boolean {
//                        adapter.submitList(currentUsers.filter { it.login.contains(query) })
//                        return true
//                    }
//                })
        }
        executeRequest()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        currentRequest?.cancel()
        _binding = null
    }

    private fun handleException(e: Throwable) {
        Toast.makeText(requireContext(), e.message ?: "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun executeRequest(
        onRequestFinished: () -> Unit = {}
    ) {
        val finishRequest = {
            onRequestFinished()
            currentRequest = null
        }
        if (currentRequest != null) return
        val since = currentPage * PER_PAGE

        currentRequest = GithubService.api
            .getUsers(since, PER_PAGE)
            .apply {
                enqueue(object : Callback<List<User>> {
                    override fun onResponse(
                        call: Call<List<User>>,
                        response: Response<List<User>>
                    ) {
                        if (response.isSuccessful) {
                            val users = response.body() ?: return
                            currentUsers.addAll(users)
                            val items = adapter.currentList
                                .dropLastWhile { it == PagingData.Loading }
                                .plus(users.map { PagingData.Item(it) })
                                .plus(PagingData.Loading)
                            adapter.submitList(items)
                            currentPage++
                        } else {
                            handleException(HttpException(response))
                        }
                        finishRequest()
                    }

                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        if (!call.isCanceled) {
                            handleException(t)
                        }
                        finishRequest()
                    }
                })
            }
    }
    companion object {
        private const val COUNT_TO_LOAD = 35
        private const val PER_PAGE = 50
        private const val RECYCLER_ITEM_SPACE = 50
    }
}