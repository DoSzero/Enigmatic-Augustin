package com.rubygames.assass.logic.model

data class Card(
    val cardImage: CardImage,
    var isVisible: Boolean = false,
    var isMatched: Boolean = false
) {
    fun getImage() : Int {
        return cardImage.image
    }
}

data class CardImage(
    val image: Int,
)