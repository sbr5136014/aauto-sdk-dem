package com.github.martoreto.aademo

class input {
    val callback = object : InputCallback {
        fun onInputSubmitted(text: String) {

            // You will receive this callback when the user presses enter on the keyboard.
        }

        fun onInputTextChanged(text: String) {
            // You will receive this callback as the user is typing. The update frequency is determined by the host.
        }
    }

}