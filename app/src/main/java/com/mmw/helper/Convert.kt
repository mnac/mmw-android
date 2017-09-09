package com.mmw.helper

/**
 * Created by Mathias on 07/09/2017.
 */
class Convert {
    companion object {
        fun safeLongToInt(l: Long): Int {
            if (l < Integer.MIN_VALUE) {
                return 0
            } else if (l > Integer.MAX_VALUE) {
                return 100
            }
            return l.toInt()
        }
    }
}