package guru.springframework.ssm.msscssm.services;

import guru.springframework.ssm.msscssm.domain.Payment;
import guru.springframework.ssm.msscssm.domain.PaymentEvent;
import guru.springframework.ssm.msscssm.domain.PaymentState;
import guru.springframework.ssm.msscssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
class PaymentServiceImplTest
{

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp()
    {
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();

    }

    @Transactional
    @Test
    void preAuth()
    {
        Payment savedPayment = paymentService.newPayment(payment);

        System.out.println("Should be NEW");
        System.out.println(savedPayment.getState());
        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());

        Payment preAuthedPayment = paymentRepository.getOne(savedPayment.getId());

        System.out.println("Should be PRE_AUTH or PRE_AUTH_ERROR");
        System.out.println(sm.getState().getId());

        System.out.println("preAuth " + preAuthedPayment);
    }

    @Transactional
    @RepeatedTest(20)
    void testAuth()
    {
        Payment savedPayment = paymentService.newPayment(payment);

        StateMachine<PaymentState, PaymentEvent> preAuthSm = paymentService.preAuth(savedPayment.getId());
        if (preAuthSm.getState().getId() == PaymentState.PRE_AUTH)
        {
            System.out.println("Payment is Pre Authorized");
            StateMachine<PaymentState, PaymentEvent> authSm = paymentService.authorizePayment(savedPayment.getId());
            System.out.println("Result of Auth: " + authSm.getState().getId());
        }
        else
        {
            System.out.println("Payment failed preAuth...");
        }
    }
}