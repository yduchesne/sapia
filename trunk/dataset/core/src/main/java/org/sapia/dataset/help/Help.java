package org.sapia.dataset.help;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sapia.dataset.algo.Criteria;
import org.sapia.dataset.func.ArgFunction;
import org.sapia.dataset.util.Data;
import org.sapia.dataset.util.Settings;
import org.sapia.dataset.util.Strings;

/**
 * Displays command-line help.
 * @author yduchesne
 *
 */
public class Help {

  private static final int INDENT = 3;
  
  private Help() {
    
  }
  
  /**
   * @param clazz the {@link Class} for which to display method help.
   * @param methodName the method name, or part of its name, to use as a matcher.
   * @return the help string.
   */
  @Doc("Returns help about one or more methods of a class (provided they match the given method name pattern)")
  public static String help(Class<?> clazz, final String methodName) {
    return help(clazz, methodName, new String[]{});
  }
  
  /**
   * @param clazz the {@link Class} for which to display method help.
   * @param methodName the method name, or part of its name, to use as a matcher.
   * @param argsMatchers the array of matcher strings to test against method parameter type names.
   * @return the help string.
   */
  @Doc("Returns help about one or more methods of a class (provided they match the given method name and argument type patterns)")
  public static String help(Class<?> clazz, final String methodName, final List<String> argsMatchers) {
    return help(clazz, methodName, argsMatchers.toArray(new String[argsMatchers.size()]));
  }
  
  /**
   * @param clazz the {@link Class} for which to display method help.
   * @param methodName the method name, or part of its name, to use as a matcher.
   * @param argsMatchers the array of matcher strings to test against method parameter type names.
   * @return the help string.
   */
  @Doc("Returns help about one or more methods of a class (provided they match the given method name and argument type patterns)")
  public static String help(Class<?> clazz, final String methodName, final String...argsMatchers) {
    final String       normalizedMethodName   = methodName.toLowerCase().trim();
    final List<String> normalizedArgsMatchers = new ArrayList<>(argsMatchers.length);
    for (String a : argsMatchers) {
      normalizedArgsMatchers.add(a.toLowerCase().trim());
    }
    List<Method> declaredMethods = filter(clazz.getDeclaredMethods(), new ArgFunction<Method, Boolean>() {
      @Override
      public Boolean call(Method m) {
        return Modifier.isPublic(m.getModifiers()) 
            && m.getName().toLowerCase().contains(normalizedMethodName)
            && (normalizedArgsMatchers.isEmpty() || matchesArgs(m.getParameterTypes(), normalizedArgsMatchers));
      }
    });
    return help(clazz, declaredMethods);
  }

  /**
   * @param  clazz the {@link Class} to generate help for.
   * @return the help documentation string.
   */
  @Doc("Returns help documentation for the given class")
  public static String help(Class<?> clazz) {
    List<Method> declaredMethods = filter(clazz.getDeclaredMethods(), new ArgFunction<Method, Boolean>() {
      @Override
      public Boolean call(Method m) {
        return Modifier.isPublic(m.getModifiers());
      }
    });
    return help(clazz, declaredMethods);
  }
  
  // --------------------------------------------------------------------------
  // Restricted methods
  
  private static String help(Class<?> clazz, List<Method> declaredMethods) {
    
    declaredMethods = Data.filter(declaredMethods, new Criteria<Method>() {
      @Override
      public boolean matches(Method v) {
        return !v.isAnnotationPresent(Hide.class);
      }
    });
    
    Collections.sort(declaredMethods, new Comparator<Method>() {
      public int compare(Method o1, Method o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    
    StringBuilder builder = new StringBuilder();
    builder.append("Class help doc: ").append(clazz.getName()).append(System.lineSeparator());
    Doc classDoc = clazz.getAnnotation(Doc.class);
    if (classDoc != null) {
      builder.append(classDoc.value()).append(System.lineSeparator());
    }

    for (int methodIndex = 0; methodIndex < declaredMethods.size(); methodIndex++) {
      Method m = declaredMethods.get(methodIndex);
      builder.append("-> ").append(m.getName());
      if (Modifier.isStatic(m.getModifiers())) {
        builder.append(" (static method)");
      }
      Doc methodDoc = m.getAnnotation(Doc.class);
      if (methodDoc != null) {
        builder.append(". ").append(methodDoc.value()).append(".");
        builder.append(System.lineSeparator());
        Example[] exampleDocs = methodDoc.examples();
        if (exampleDocs != null && exampleDocs.length > 0) {
          builder.append(Strings.repeat(" ", INDENT)).append("Examples:").append(System.lineSeparator());
          for (Example eg : exampleDocs) {
            String name = eg.caption();
            if (!Strings.isNullOrEmpty(name)) {
              builder.append(Strings.repeat(" ", INDENT * 2)).append(name).append(": ");
            } else {
              builder.append(Strings.repeat(" ", INDENT * 2));
            }
            builder.append(eg.content()).append(System.lineSeparator());
          }
          builder.append(System.lineSeparator());
        }
      } else {
        builder.append(System.lineSeparator());
      }

      for (int argIndex = 0; argIndex < m.getParameterTypes().length; argIndex++) {
        if (argIndex == 0) {
          builder.append(Strings.repeat(" ", INDENT)).append("Parameters:").append(System.lineSeparator());
        }
        Class<?> arg = m.getParameterTypes()[argIndex];
        String argType = arg.isArray() ? "" + arg.getComponentType().getName() + "[]" : arg.getName();
        SettingsDoc settingsDoc = getParamAnnotation(m, argIndex, SettingsDoc.class);
        builder.append(Strings.repeat(" ", INDENT * 2)).append(argType);
        Doc argDoc = getParamAnnotation(m, argIndex, Doc.class);
        if (argDoc != null) {
          builder.append(": ").append(argDoc.value());
        }
        if (settingsDoc == null) {
          builder.append(System.lineSeparator());
        } else {
          builder.append(". Required/accepted settings:").append(System.lineSeparator());
          try {
            Field settingsField = clazz.getDeclaredField(settingsDoc.value());
            settingsField.setAccessible(true);
            Settings settings = (Settings) settingsField.get(null);
            builder.append(settings.toString(INDENT * 3));
          } catch (NoSuchFieldException e) {
            throw new IllegalStateException(String.format("Class %s has not field with name: %s", clazz.getName(), settingsDoc.value()));
          } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("Field %s of class %s not accessible", settingsDoc.value(), clazz.getName()));
          }
        } 
      }
      builder.append(System.lineSeparator());
    }
    return builder.toString();
  }
  
  /**
   * @param toFilter the array of methods to filter.
   * @param criterion the {@link ArgFunction} serving as criterion.
   * @return the {@link List} of methods that were "accepted" by the given function.
   */
  private static List<Method> filter(Method[] toFilter, ArgFunction<Method, Boolean> criterion) {
    List<Method> methods = new ArrayList<>();
    for (Method m : toFilter) {
      if (criterion.call(m)) {
        methods.add(m);
      }
    }
    return methods;
  }

  /**
   * @param m the method whose parameter annotation should be returned.
   * @param argIndex the index of the parameter.
   * @param annotationType the type of the expected annotation.
   * @return an {@link Annotation} of the given type.
   */
  private static <A extends Annotation> A getParamAnnotation(Method m, int argIndex, Class<A> annotationType) {
    Annotation[] annotations = m.getParameterAnnotations()[argIndex];
    for (Annotation a  : annotations) {
      if (annotationType.isAssignableFrom(a.getClass())) {
        return annotationType.cast(a);
      }
    }
    return null;
  }
  
  /**
   * @param args an array of {@link Class} instances corresponding to the method parameter
   * types to be matched.
   * @param matchers the {@link List} of matcher strings to use for matching.
   * @return <code>true</code> if any of the given argument types is matched by 
   * any one of the given matcher strings.
   */
  private static boolean matchesArgs(Class<?>[] args, List<String> matchers) {
    for (Class<?> arg : args) {
      for (String matcher : matchers) {
        if (arg.getName().toLowerCase().contains(matcher)) {
          return true;
        }
      }
    }
    return false;
  }
}
