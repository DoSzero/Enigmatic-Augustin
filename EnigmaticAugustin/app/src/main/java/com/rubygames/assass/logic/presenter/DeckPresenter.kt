package com.rubygames.assass.logic.presenter

import android.os.Handler
import android.os.Looper
import com.rubygames.assass.R
import com.rubygames.assass.logic.model.Card
import com.rubygames.assass.logic.model.CardImage
import com.rubygames.assass.logic.presenter.DeckInterface.*
import com.rubygames.assass.logic.adapter.DeckAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class DeckPresenter (view: View) : Presenter {

    private var mView: View = view
    private var itemsCount = 12
    private var stepCount: Int = 0
    private var items: ArrayList<Card> = arrayListOf()
    private var images: ArrayList<CardImage> = arrayListOf()
    private var visibles = arrayListOf<Int>()
    private var isCLickable = true

    init {
        mView.bind()
        fillList()
    }

    override fun fillInitial() {
        var x = 0
        while (x < itemsCount) {
            val image = images[Random.nextInt(images.size)]
            if (countOf(Card(image)) < 2) {
                items.add(Card(image))
                items.add(Card(image))
                x+=2
            }
        }
        items.shuffle()
    }

    override fun beginGame() {
        fillInitial()
        mView.startGame()
        stepCount = 0
    }

    override fun fillList() {
        images.add(CardImage(R.drawable.elem1))
        images.add(CardImage(R.drawable.elem2))
        images.add(CardImage(R.drawable.elem3))
        images.add(CardImage(R.drawable.elem5))
        images.add(CardImage(R.drawable.elem6))
        images.add(CardImage(R.drawable.elem7))
    }

    override fun getAdapter(): DeckAdapter {
        val deckAdapter = DeckAdapter(items, mView.getContext()) {
            if (isCLickable) {
                if (!items[it].isVisible) {
                        items[it].isVisible = true
                        visibles.add(it)
                        mView.refreshData(it)
                    if (visibles.size == 2) {
                        isCLickable = false

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (items[visibles[0]].getImage() == items[visibles[1]].getImage()) {
                                items[visibles[0]].isMatched = true
                                items[visibles[1]].isMatched = true
                                mView.showToast("Yay!")
                            } else {
                                items[visibles[0]].isVisible = false
                                items[visibles[1]].isVisible = false
                            }
                            mView.refreshData(visibles[0])
                            mView.refreshData(visibles[1])
                            visibles.clear()
                            isCLickable = true
                            checkGameEnd()
                        }, 1000L)
                        stepCount++
                    }
                    mView.updateSteps(stepCount)
                }
            }
        }
        return deckAdapter
    }

    private fun checkGameEnd() {
        var i = 0
        for(k in 0 until items.size) {
            if(items[k].isMatched) i++
        }
        if(i == items.size) {
            mView.showEnding()
            items.clear()
        }
    }

    private fun countOf(item: Card): Int {
        return Collections.frequency(items, item)
    }
}