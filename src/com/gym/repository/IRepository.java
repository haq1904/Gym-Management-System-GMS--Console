package com.gym.repository;

import java.util.List;

// Chữ <T> là Generic, đại diện cho bất kỳ Object nào (User, GymMachine, Schedule...)
public interface IRepository<T> {

    // Hàm tải dữ liệu từ file lên List
    List<T> loadData();

    // Hàm lưu toàn bộ List xuống file
    void saveData(List<T> dataList);

    // Hàm thêm mới một đối tượng vào List và lưu file
    boolean add(List<T> dataList, T entity);

    // Hàm xóa một đối tượng dựa trên ID hoặc Username
    boolean delete(List<T> dataList, String identifier);
}