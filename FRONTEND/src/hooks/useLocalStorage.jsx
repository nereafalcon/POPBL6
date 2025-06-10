import { useState } from "react";

//  The useLocalStorage hook returns an array with two elements:
//     1. T: The current value stored in local storage (or the initial value if nothing is stored yet).
//          - This represents the state of the hook.
// 	        - Its type is T, a generic placeholder that will be replaced by the actual type when the hook is used.
//
//     2. (value: T | ((prev: T) => T)) => void: A function to update the value in local storage and the state.
// 	      The function accepts either:
//          - A new value of type T to directly set as the state.
// 	        - A function (prev: T) => T that takes the current state (prev) as input and returns the new state. This is
// 	          useful for cases where the new state depends on the previous one (e.g., incrementing a counter).

const useLocalStorage = (key, initialValue) => {

    const [storedValue, setStoredValue] = useState(() => {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : initialValue;
        } catch (error) {
            console.error("Error reading localStorage key:", key, error);
            return initialValue;
        }
    });

    const setValue = (value) => {
        try {
            const valueToStore = value instanceof Function ? value(storedValue) : value;
            localStorage.setItem(key, JSON.stringify(valueToStore));
            setStoredValue(valueToStore);
        } catch (error) {
            console.error("Error setting localStorage key:", key, error);
        }
    };

    return [storedValue, setValue];

};

export default useLocalStorage;