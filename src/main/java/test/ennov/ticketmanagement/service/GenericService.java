package test.ennov.ticketmanagement.service;

import test.ennov.ticketmanagement.utils.exceptions.NoDataFoundException;

import java.util.List;

public interface GenericService<T> {
    List<T> getAllElements() throws NoDataFoundException;
    T createElement(T element);
}
