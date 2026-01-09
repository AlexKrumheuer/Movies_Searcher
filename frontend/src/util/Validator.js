export default class Validator {
    static validateText(text) {
        return text && text.trim().length > 0
    }

    static validateEmail(email) {
        const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
        return regex.test(email)
    }

    static validatePassword(password) {
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*^#()\[\]{}|\\/+_.:;=~])[^\s<>]{8,}$/
        return regex.test(password)
    }

    static validateEqualsPassword(password, confirmPassword) {
        if(password != confirmPassword) return false
        return true
    }
}