package priv.yanyang.webim.common;

public class TicketThread extends Thread{

    private int ticket = 10;

    public void run(){
        for(int i =0;i<10;i++){
            ///*synchronized (this)*/{
                if(this.ticket>0){
                    //try {
                   //     Thread.sleep(100);
                        System.out.println(Thread.currentThread().getName()+"卖票---->"+(this.ticket--));
                   //
                }
            //}
        }
    }

    public static void main(String[] arg){
        TicketThread t1 = new TicketThread();
        //new Thread(t1,"线程1").start();
        //new Thread(t1,"线程2").start();
        t1.start();


        TicketThread t2 = new TicketThread();
        //new Thread(t1,"线程1").start();
        //new Thread(t1,"线程2").start();
        t2.start();
        System.out.println("end");


        //也达到了资源共享的目的，此处网上有各种写法，很多写法都是自圆其说，举一些特殊例子来印证自己的观点，然而事实却不尽如此。
    }
}
