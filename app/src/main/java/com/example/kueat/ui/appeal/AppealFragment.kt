package com.example.kueat.ui.appeal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kueat.databinding.FragmentAppealBinding

class AppealFragment : Fragment() {

    lateinit var binding: FragmentAppealBinding
    lateinit var adapter: AppealAdapter
    lateinit var dataList: ArrayList<AppealArticle>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppealBinding.inflate(inflater, container, false)

        initData()
        adapter = AppealAdapter(dataList)
        binding.rvAppealArticle.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvAppealArticle.adapter = adapter

        return binding.root
    }

    fun initData(){
        dataList = arrayListOf(
            AppealArticle(
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1
            ),
            AppealArticle(
                0,
                "알촌",
                "말해 뭐함",
                2,
                1
            ),
            AppealArticle(
                0,
                "파란만잔",
                "카카오 아메리카노 꼭 먹어보셈",
                1,
                4
            ),
            AppealArticle(
                0,
                "쌍둥이네 칼국수",
                "무난함",
                0,
                1
            ),
            AppealArticle(
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1
            ),
            AppealArticle(
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1
            ),
            AppealArticle(
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1
            ),
            AppealArticle(
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1
            ),
            AppealArticle(
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1
            ),
            AppealArticle(
                0,
                "후문 카레 덮밥 맛집 코코도리",
                "이 집 카레가 맛있습니다",
                1,
                1
            )
        )
    }
}