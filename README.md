# HuluwaWar
151220004 曾冰洁 zengzbj@foxmail.com
![](https://github.com/zengbingjie/HuluwaGUI/raw/master/Screenshots/1.png)

## 配置
* Junit4.12
* jdk 1.8

## 游戏说明
* 按SPACE开始游戏，按R回到开始界面，按S保存战斗画面记录，按L选择记录文件并回放；
* 打斗中各个角色可游走在空间**任意位置**，爷爷和蛇精不参与战斗，不杀人也不会被杀，只会上下移动指挥己方阵营；
* 双方有肢体接触之后会打一会，他们有各自的血量，hp减到零及以下就会死亡，留下尸体在场上；
* 邪恶的角色比较傻，蝎子精在空间中随意游走，蛤蟆只会往前走，公平起见他们比葫芦娃肉厚一点，毕竟葫芦娃又聪明跑得又快胜率又高；
* 在我对各角色的速度、初始hp、胜率等配置的情况下，总得来说葫芦娃赢面大，但也不会经常躺赢。
![](https://github.com/zengbingjie/HuluwaGUI/raw/master/Screenshots/2.png)
* 回放文件保存在工程文件下的**Records**文件夹中，选择recordxxxxxx.txt文件，xxxxxx是保存文件的时间，回放完后按R回到开始界面，也可以在回放当中回到开始界面；
* 保存和读取后都会有对话框提示成功或失败；
![](https://github.com/zengbingjie/HuluwaGUI/raw/master/Screenshots/4.png)
* 游戏结束后会显示谁赢了，按R回到开始界面，按S保存记录。

## 实现方式
* 每个生物都是一个线程
* 记录战斗画面是一个线程
* 回放记录是一个线程

## 面向对象程序设计

#### 1. 封装抽象
* 沿袭之前的小作业，每个生物都继承了**抽象类**`Creature`，此次涉及图形化界面，`Creature`又继承了`Thing2D`类。
* 当我发现自己在两个不同的类中都写了一些有关生物想要移动到`field`中某个位置时，判断此坐标是否符合逻辑的函数，就感觉到应该把这些都抽象出来，减少代码耦合，于是就有了`MoveChecker`类。

#### 2. SRP单一职责原则、OCP开放封闭原则
* 用`HuluwaWarFormation`实现`Formation`接口的`format()`方法，用来完成对游戏角色位置的**阵型初始化**，排阵型的任务不应该交给Field，于是抽象出一个类来管理这个任务，需要时可以单独修改HuluwaWarFormation类。
![](https://github.com/zengbingjie/HuluwaGUI/raw/master/Screenshots/6.png)
* 每个`Creature`线程在`run()`中**移动的策略**也抽象成了一个接口类`MoveStrategy`，不同的角色可以有自己的策略类，这样想修改移动策略时就不用再修改各个`Creature`类中的具体实现，只需要策略类来实现并告诉生物应该如何移动。比如爷爷和蛇精的移动模式实际上是一样的，都是一个指挥者的移动策略`ConductorMoveStategy`。
![](https://github.com/zengbingjie/HuluwaGUI/raw/master/Screenshots/5.png)

## 相关技术
* 线程池
* 文件IO操作
* Timer
* IDEA IntelliJ多线程debug
* 使用匿名函数写TimerTask
* 单元测试
* Maven

## 遇到的问题及解决方案

1. 在开始的算法下，有时候葫芦娃会卡在一个地方不动了，改进算法后，如果卡住，过一会会它自己会走开重新去寻找敌人。但偶尔还是会有葫芦娃傻了的时候，就是两只或者几只葫芦娃卡在一起，都动不了了。由于移动策略的算法暂时不是重点，所以没有选择优化算法，而选择了让`field`监视本次游戏进行了多久，**如果超过30秒，游戏自动结束。**世界和平。
2. 本来想让生物A遇到B时，A让自己和B都`sleep()`一会，以表示在战斗之中，但是线程A和B没有父子关系，应该没有A让B睡觉这种操作……于是给`Creature`加上了`hp`属性，每次打架造成一定伤害，打着打着就有一方死了，这样实现视觉上**双方对峙**了一会的效果。
3. `record`**读写不同步**问题。回放时，`replayer`线程在写`record`给主线程，主线程读取`record`来显示画面。我的回放功能有些闪屏，猜测是读写不同步造成的，debug也发现主线程读取的时候丢失了一些记录。由于我的实现是field中存了一个`record`的**list**，传给`replayer`，一个读一个写，而`synchronized`是控制多个线程对一个对象/方法的访问，而我这里有一组`record`，两个方法，无法实现。我又考虑用`join()`，因为`repaint()`需要等待`replayer`的结果，
但我又发现，`replayer`也需要`repaint()`结束后再继续循环。这么一想，似乎是应该用`wait()`和`notifyAll()`，并实现一个`RecordBuffer`类，里面装一个record的list，并提供读和写的同步方法。这样改完好像好多了，调试了一下看到读和写至少同步了。但画面还是偶尔会闪，不知道为什么。
4. 蝎子精本来和蛤蟆一样只会匀速往前走，画面不够生动，于是我让蝎子精变聪明了，可以随机上下左右移动，场面更加生动一些了。

## 不足
1. 游戏启动之后再拖动改变窗口大小的话，`Creature`的图片绘制不会随之按比例变化大小，可能会越出窗口范围之外。记录文件的时候也没有记录界面大小，如果当前界面大小配置与记录生成时的大小不同，无法适应新界面大小。
2. 只有一次遇到，打架打到一半，一只蛤蟆在判断游戏是否结束时，`field`中`gameHasOver()`方法访问`world`居然溢出，错误显示当时`world`大小为零，然后蛤蟆就被打断不动了，最后站在那里被葫芦娃打。这一点我实在是想不通，明明只有游戏开始前和结束后`field`自己会增删`world`，其他时候用到`world`都只是以**get**形式访问，没有修改和删除的情况。多线程的bug难以再次捕捉，最后没有解决这个问题。
3. 最后还有一个问题不是很懂，在葫芦娃需要找敌人，遍历world时：
```java
ArrayList world = field.getWorld();
for (int i = 0; i < world.size(); i++) {
    Thing2D item = (Thing2D) world.get(i);
    if (item instanceof Creature){ ... }
}
```
这段代码如果换成
```java
ArrayList world = field.getWorld();
for(Object item: world){
    if (item instanceof Creature){ ... }
}
```
就会发生`ConcurrentModificationException`异常。查了查资料还是不很清楚为什么。不明白的原因和上一点中提到的一样。
