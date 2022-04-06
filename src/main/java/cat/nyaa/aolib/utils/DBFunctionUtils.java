package cat.nyaa.aolib.utils;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DBFunctionUtils {
    private static final MethodHandles.Lookup DEFAULT_METHOD_LOOKUP = MethodHandles.lookup();

    @Contract(pure = true)
    public static <T> @NotNull BiFunction<Integer, ResultSet, @Nullable T> getFirstAutoGeneratedData(Class<T> type) {
        return (count, rs) -> {
            if (count <= 0) return null;
            try {
                return rs.getObject(1, type);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    public static <T> @NotNull Function<ResultSet, @Nullable T> getFirstData(Class<T> type) {
        return (rs) -> {
            try {
                if (rs.next()) {
                    return rs.getObject(1, type);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    public static <T> @NotNull Function<ResultSet, @NotNull List<T>> getDataListFromResultSet(Class<T> dataClass) {
        MethodHandle finalMethodHandle = findFromResultSetMethodHandle(dataClass);
        return (rs) -> {

            List<T> list = Lists.newArrayList();
            try {
                while (rs.next()) {
                    @SuppressWarnings("unchecked")
                    var data = (T) finalMethodHandle.invoke(rs);
                    list.add(data);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return list;
        };
    }

    public static <T> @NotNull Function<ResultSet, @Nullable T> getDataFromResultSet(Class<T> dataClass) {
        MethodHandle finalMethodHandle = findFromResultSetMethodHandle(dataClass);
        return (rs) -> {

            try {
                if (rs.next()) {
                    @SuppressWarnings("unchecked")
                    var result = (T) finalMethodHandle.invoke(rs);
                    return result;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    private static @NotNull MethodHandle findFromResultSetMethodHandle(Class<?> dataClass) {
        String[] methodName = {"fromResultSet", "createFromResultSet", "create"};
        MethodHandle methodHandle = null;
        for (String s : methodName) {
            if (methodHandle == null) {
                try {
                    methodHandle = DEFAULT_METHOD_LOOKUP.findStatic(dataClass, s, MethodType.methodType(dataClass, ResultSet.class));
                } catch (NoSuchMethodException | IllegalAccessException ignored) {
                }
            }
        }
        if (methodHandle == null) {
            try {
                methodHandle = DEFAULT_METHOD_LOOKUP.findConstructor(dataClass, MethodType.methodType(void.class, ResultSet.class));
            } catch (NoSuchMethodException | IllegalAccessException ignored) {
            }
        }
        if (methodHandle == null) {
            throw new RuntimeException("No method or constructor found in " + dataClass.getName() + " to create data from ResultSet");
        }
        return methodHandle;
    }
}
