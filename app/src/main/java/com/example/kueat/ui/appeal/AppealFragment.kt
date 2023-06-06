package com.example.kueat.ui.appeal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.databinding.FragmentAppealBinding

class AppealFragment : Fragment() {

    lateinit var binding: FragmentAppealBinding
    lateinit var adapter: AppealArticleAdapter
    lateinit var dataList: ArrayList<AppealArticleInfo>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppealBinding.inflate(inflater, container, false)
        initData()
        initLayout()

        return binding.root
    }

    fun initLayout(){
        adapter = AppealArticleAdapter(dataList)
        binding.rvAppealArticle.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter.OnItemClickListener = object: AppealArticleAdapter.onItemClickListener {
            override fun onItemClicked(position: Int) {
                val i =  Intent(requireContext(), AppealArticleDetailActivity::class.java)
                requireActivity().startActivity(i)
            }
        }
        binding.rvAppealArticle.adapter = adapter
        binding.llAddAppeal.setOnClickListener {
            val i =  Intent(requireContext(), EditAppealArticleActivity::class.java)
            requireActivity().startActivity(i)
        }
    }

    fun initData(){
        dataList = arrayListOf(
            AppealArticleInfo(
                0,
                0,
                0,
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1,
                "05/12 20:12"
            ),
            AppealArticleInfo(
                0,
                0,
                0,
                0,
                "알촌",
                "말해 뭐함",
                2,
                1,
                "05/12 20:12"
            ),
            AppealArticleInfo(
                0,
                0,
                0,
                0,
                "파란만잔",
                "카카오 아메리카노 꼭 먹어보셈",
                1,
                4,
                "05/12 20:12"
            ),
            AppealArticleInfo(
                0,
                0,
                0,
                0,
                "쌍둥이네 칼국수",
                "무난함",
                0,
                1,
                "05/12 20:12"
            ),
            AppealArticleInfo(
                0,
                0,
                0,
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1,
                "05/12 20:12"
            ),
            AppealArticleInfo(
                0,
                0,
                0,
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1,
                "05/12 20:12"
            ),
            AppealArticleInfo(
                0,
                0,
                0,
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1,
                "05/12 20:12"
            ),
            AppealArticleInfo(
                0,
                0,
                0,
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1,
                "05/12 20:12"
            ),
            AppealArticleInfo(
                0,
                0,
                0,
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1,
                "05/12 20:12"
            ),
            AppealArticleInfo(
                0,
                0,
                0,
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1,
                "05/12 20:12"
            )
        )
    }
}