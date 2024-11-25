package framework.http.input;

import framework.exceptions.internal.InternalException;
import framework.exceptions.request.RequestParameterScanningException;

import java.util.*;
import java.util.function.Function;

public class InputScanners {
    private static final Map<Class<?>, InputMapper<?>> inputMappers = Map.of(
            int.class, createMapper(Scanner::nextInt),
            long.class, createMapper(Scanner::nextLong),
            double.class, createMapper(Scanner::nextDouble),
            float.class, createMapper(Scanner::nextFloat),
            String.class, createMapper(Scanner::next)
    );

    private static <T> InputMapper<T> createMapper(Function<Scanner, T> function){
        return function::apply;
    }

    private interface InputMapper<T> {
        T scan(Scanner scanner) throws NoSuchElementException, IllegalStateException;
    }

    public static Object[] parseInput(String url, List<Class<?>> expectedClasses) {
        Scanner scanner = new Scanner(url);
        scanner.useDelimiter("/");

        List<Object> results = new ArrayList<>();
        for(int i = 0; i < expectedClasses.size(); i++){
            try{
                InputMapper<?> mapper = inputMappers.get(expectedClasses.get(i));
                results.add(mapper.scan(scanner));
            } catch (NullPointerException e){
                throw RequestParameterScanningException.unsupportedType(expectedClasses.get(i));
            } catch (InputMismatchException e){
                throw RequestParameterScanningException.parseException(url, i, expectedClasses);
            } catch (NoSuchElementException e){
                throw RequestParameterScanningException.tooFewArgumentsException(url, expectedClasses);
            } catch (IllegalStateException e){
                throw new InternalException("Scanner closed");
            }
        }

        if(scanner.hasNext()){
            throw RequestParameterScanningException.tooManyArgumentsException(url, expectedClasses);
        }

        scanner.close();
        return results.toArray();
    }
}
