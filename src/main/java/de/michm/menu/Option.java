/*
 * Copyright (c) 2022. Manfred Michaelis <mm@michm.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.michm.menu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * The Option class is the representation of an option in the menu.
 * It will be created in the ConsoleMenu constructor from an annotated
 * method of the derived class.
 */
public class Option {
    private String name;
    private String pattern;
    private Method action;

    /**
     * The constructor expects 3 parameters: The name of the option,
     * the pattern of input and the method, which will be invoked.
     *
     * @param name <String>
     * @param pattern <String>
     * @param action <Method>
     */
    Option(String name, String pattern, Method action) {
        this.name = name;
        this.pattern = pattern;
        this.action = action;
    }

    /**
     * Returns the name.
     *
     * @return <String>
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the pattern.
     *
     * @return <String>
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Returns the action method.
     *
     * @return <Method>
     */
    public Method getAction() {
        return action;
    }

    /**
     * Uses Java reflection to create an instance of ConsoleMenu and invokes
     * the method which reference is stored in action. The Object array args
     * will be passed.
     *
     * @param args <Object[]>
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object invoke(AssmusMenu instance, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return action.invoke(instance, args);
    }

    /**
     * Returns parameter count of the method.
     *
     * @return <int>
     */
    int getParameterCount() {
        return action.getParameterCount();
    }

    /**
     * Returns an array of parameter types
     *
     * @return <Class<?>[]>
     */
    Class<?>[] getParameterTypes() {
        return action.getParameterTypes();
    }

    /**
     * Returns the return type
     *
     * @return <Class<?>>
     */
    Class<?> getReturnType() {
        return action.getReturnType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option option = (Option) o;
        return getName().equals(option.getName())
                && getPattern().equals(option.getPattern())
                && getAction().equals(option.getAction());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPattern(), getAction());
    }

    @Override
    public String toString() {
        return "Option{" +
                "name='" + name + '\'' +
                ", pattern='" + pattern + '\'' +
                ", action=" + action +
                '}';
    }
}
