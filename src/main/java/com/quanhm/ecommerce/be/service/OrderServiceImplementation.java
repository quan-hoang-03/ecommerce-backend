package com.quanhm.ecommerce.be.service;

import com.quanhm.ecommerce.be.exception.OrderException;
import com.quanhm.ecommerce.be.model.*;
import com.quanhm.ecommerce.be.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImplementation implements OrderService {

    private OrderRepository orderRepository;
    private CartService cartService;
    private AddressRepository addressRepository;
    private UserRepository userRepository;
    private OrderItemService orderItemService;
    private OrderItemRepository orderItemRepository;
    private ProductRepository productRepository;

    public OrderServiceImplementation(OrderRepository orderRepository, CartService cartService, AddressRepository addressRepository, UserRepository userRepository,OrderItemService orderItemService, OrderItemRepository orderItemRepository, ProductRepository productRepository){
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderItemService = orderItemService;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Order createOrder(User user, Address shippingAddress) {
//        Gán địa chỉ giao hàng cho người dùng, lưu địa chỉ vào CSDL,
//        rồi cập nhật lại thông tin user có thêm địa chỉ đó.
        shippingAddress.setUser(user);
        Address address = addressRepository.save(shippingAddress);
        user.getAddress().add(address);
        userRepository.save(user);
//        Lấy giỏ hàng hiện tại của người dùng,
//        và lưu các mặt hàng vào danh sách orderItems trong đơn hàng
        Cart cart = cartService.findUserCart(user.getId());
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();

            orderItem.setPrice(item.getPrice());
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSize(item.getSize());
            orderItem.setUserId(item.getUserId());
            orderItem.setDiscountedPrice(item.getDiscountedPrice());

            OrderItem createdOrderItem = orderItemRepository.save(orderItem);
            orderItems.add(createdOrderItem);
        }
        Order createdOrder = new Order();
        createdOrder.setUser(user);
        createdOrder.setOrderItems(orderItems);
        createdOrder.setTotalPrice(cart.getTotalPrice());
        createdOrder.setTotalDiscountedPrice(cart.getTotalDiscountedPrice());
        createdOrder.setDiscount(cart.getDiscount());
        createdOrder.setTotalItem(cart.getTotalItem());
        createdOrder.setShippingAddress(address);
        createdOrder.setOrderDate(LocalDateTime.now());
        createdOrder.setOrderStatus("PENDING");
        createdOrder.setCreatedAt(LocalDateTime.now());
        createdOrder.getPaymentDetails().setPaymentStatus("PENDING");

        // Xử lý phần thanh toán an toàn
//        PaymentDetails paymentDetails = new PaymentDetails();
//        paymentDetails.setStatus("PENDING");
//        createdOrder.setPaymentDetails(paymentDetails);

        Order savedOrder = orderRepository.save(createdOrder);

        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);
        }

        return savedOrder;
    }

    @Override
    public Order findOrderById(Long orderId) throws OrderException {
        Optional<Order> opt = orderRepository.findById(orderId);

        if (opt.isPresent()) {
            return opt.get();
        }

        throw new OrderException("Không tìm thấy đơn hàng với ID: " + orderId);
    }

    @Override
    public List<Order> usersOrderHistory(Long userId) {
        List<Order> orders = orderRepository.getUsersOrders(userId);
        return orders;
    }

    @Override
    public Order placeOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("PLACED");
        order.getPaymentDetails().setPaymentStatus("COMPLETED");
        return order;
    }

    @Override
    public Order confirmOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("CONFIRMED");
        Order savedOrder = orderRepository.save(order);
        
        // Cập nhật kho hàng khi đơn hàng được xác nhận
        updateInventoryOnOrderConfirmed(order);
        
        return savedOrder;
    }
    
    // Hàm cập nhật kho hàng khi đơn hàng được xác nhận
    private void updateInventoryOnOrderConfirmed(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            
            // Giảm số lượng trong kho
            int newQuantity = product.getQuantity() - orderItem.getQuantity();
            if (newQuantity < 0) {
                newQuantity = 0; // Đảm bảo không âm
            }
            product.setQuantity(newQuantity);
            
            // Tăng số lượng đã bán
            product.setSoldQuantity(product.getSoldQuantity() + orderItem.getQuantity());
            
            productRepository.save(product);
        }
    }

    @Override
    public Order shipOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("SHIPPED");
        return orderRepository.save(order);
    }

    @Override
    public Order deliverOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        order.setOrderStatus("DELIVERED");
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        
        // Nếu đơn hàng đã được confirm (đã trừ kho), thì trả lại kho
        if ("CONFIRMED".equals(order.getOrderStatus()) || 
            "SHIPPED".equals(order.getOrderStatus()) || 
            "DELIVERED".equals(order.getOrderStatus())) {
            restoreInventoryOnOrderCancel(order);
        }
        
        order.setOrderStatus("CANCELLED");
        return orderRepository.save(order);
    }
    
    // Hàm trả lại kho hàng khi đơn hàng bị hủy
    private void restoreInventoryOnOrderCancel(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            
            // Trả lại số lượng vào kho
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());
            
            // Giảm số lượng đã bán
            int newSoldQuantity = product.getSoldQuantity() - orderItem.getQuantity();
            if (newSoldQuantity < 0) {
                newSoldQuantity = 0; // Đảm bảo không âm
            }
            product.setSoldQuantity(newSoldQuantity);
            
            productRepository.save(product);
        }
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void deleteOrder(Long orderId) throws OrderException {
        Order order = findOrderById(orderId);
        orderRepository.deleteById(orderId);
    }

    @Override
    public Order updateOrderAddress(Long orderId, Long addressId) throws OrderException {
        Order order = findOrderById(orderId);
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new OrderException("Không tìm thấy địa chỉ với ID: " + addressId));
        order.setShippingAddress(address);
        return orderRepository.save(order);
    }
}
