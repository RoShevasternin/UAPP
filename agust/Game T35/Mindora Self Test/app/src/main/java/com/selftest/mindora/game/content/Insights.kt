package com.selftest.mindora.game.content

// ----------------------------------------------------------------------------
// Insights — короткі рефлексивні рядки для APanelItemInsight.
//
//   ДОВЖИНА: лейбл 248x28 при 12f Montserrat Regular — це ~2 рядки.
//   Тримай нові інсайти в межах ~60 символів, інакше текст обріжеться
//   по висоті (wrap=true, але висота фіксована констрейнтом).
//
//   ВИБІР: не чистий random. Використовується «мішок» — усі 20 віддаються
//   у випадковому порядку, потім тасуються заново. Так само значення
//   не з'явиться двічі підряд і юзер не подумає, що панель зависла.
// ----------------------------------------------------------------------------

object Insights {

    val list = listOf(
        "Self-awareness is the quiet start of every real change.",
        "You are not your first reaction. You are what you do next.",
        "Naming a feeling makes it smaller than it was a moment ago.",
        "The pattern you keep repeating is asking to be understood.",
        "Rest is not a reward for finishing. It is part of the work.",
        "You can hold two truths at once. Most people do.",
        "What you avoid tends to grow. What you face tends to shrink.",
        "Your strengths, overused, become what you struggle with.",
        "Curiosity about yourself works better than judgement.",
        "Small honest answers reveal more than big vague ones.",
        "Boundaries are not walls. They are directions.",
        "You learn who you are by watching what you choose.",
        "Comparison hides how far you have already come.",
        "The story you tell about yourself is not the only version.",
        "Change rarely feels like progress while it is happening.",
        "Being understood starts with understanding yourself.",
        "Not every thought deserves your full attention.",
        "You are allowed to outgrow who you used to be.",
        "Asking for help is a skill, not a weakness.",
        "Knowing your limits is a form of self-respect.",
    )

    private val bag = ArrayList<String>(list.size)
    private var last: String? = null

    /** Наступний інсайт. Гарантовано не збігається з попереднім. */
    fun next(): String {
        if (bag.isEmpty()) refill()
        val insight = bag.removeAt(bag.lastIndex)
        last = insight
        return insight
    }

    private fun refill() {
        bag.clear()
        bag.addAll(list.shuffled())

        // Не даємо новому мішку почати з того ж рядка, яким закінчився старий
        if (bag.size > 1 && bag.last() == last) {
            val i = bag.lastIndex
            val tmp = bag[i]
            bag[i] = bag[0]
            bag[0] = tmp
        }
    }
}