#一、 async await
async / await是ES7的重要特性之一，也是目前社区里公认的优秀异步解决方案。
目前，async / await这个特性已经是stage 3的建议，可以看看TC39的进度，
本篇文章将分享async / await是如何工作的，Promise、generator、yield等ES6的相关知识。

在详细介绍async / await之前，先回顾下目前在ES6中比较好的异步处理办法。
下面的例子中数据请求用Node.js中的request模块，数据接口采用Github v3 api文档提供的repo代码仓库
详情API作为例子演示。

## 1、Promise对异步的处理

虽然Node.js的异步IO带来了对高并发的良好支持，同时也让“回调”成为灾难，很容易造成回调地狱。
传统的方式比如使用具名函数，虽然可以减少嵌套的层数，让代码看起来比较清晰。
但是会造成比较差的编码和调试体验，你需要经常使用用ctrl + f去寻找某个具名函数的定义，
这使得IDE窗口经常上下来回跳动。使用Promise之后，可以很好的减少嵌套的层数。
另外Promise的实现采用了状态机，在函数里面可以很好的通过resolve和reject进行流程控制，
你可以按照顺序链式的去执行一系列代码逻辑了。下面是使用Promise的一个例子：
```ecmascript 6
const request = require('request');
// 请求的url和header
const options = {
  url: 'https://api.github.com/repos/cpselvis/zhihu-crawler',
  headers: {
    'User-Agent': 'request'
  }
};
// 获取仓库信息
const getRepoData = () => {
  return new Promise((resolve, reject) => {
    request(options, (err, res, body) => {
      if (err) {
        reject(err);
      }
      resolve(body);
    });
  });
};

getRepoData()
  .then((result) => console.log(result))
  .catch((reason) => console.error(reason));

// 此处如果是多个Promise顺序执行的话，如下：
// 每个then里面去执行下一个promise
// getRepoData()
//   .then((value2) => {return promise2})
//   .then((value3) => {return promise3})
//   .then((x) => console.log(x))

```

不过Promise仍然存在缺陷，它只是减少了嵌套，并不能完全消除嵌套。举个例子，
对于多个promise串行执行的情况，第一个promise的逻辑执行完之后，
我们需要在它的then函数里面去执行第二个promise，这个时候会产生一层嵌套。
另外，采用Promise的代码看起来依然是异步的，如果写的代码如果能够变成同步该多好啊！

## 2、Generator对异步的处理

谈到generator，你应该不会对它感到陌生。在Node.js中对于回调的处理，
我们经常用的TJ / Co就是使用generator结合promise来实现的，co是coroutine的简称，
借鉴于python、lua等语言中的协程。它可以将异步的代码逻辑写成同步的方式，
这使得代码的阅读和组织变得更加清晰，也便于调试。
```ecmascript 6
const co = require('co');
const request = require('request');

const options = {
  url: 'https://api.github.com/repos/cpselvis/zhihu-crawler',
  headers: {
    'User-Agent': 'request'
  }
};
// yield后面是一个生成器 generator
const getRepoData = function* () {
  return new Promise((resolve, reject) => {
    request(options, (err, res, body) => {
      if (err) {
        reject(err);
      }
      resolve(body);
    });
  });
};

co(function* () {
  const result = yield getRepoData;
  // ... 如果有多个异步流程，可以放在这里，比如
  // const r1 = yield getR1;
  // const r2 = yield getR2;
  // const r3 = yield getR3;
  // 每个yield相当于暂停，执行yield之后会等待它后面的generator返回值之后再执行后面其它的yield逻辑。
  return result;
}).then(function (value) {
  console.log(value);
}, function (err) {
  console.error(err.stack);
});
```

## 3、async / await对异步的处理

虽然co是社区里面的优秀异步解决方案，但是并不是语言标准，
只是一个过渡方案。ES7语言层面提供async / await去解决语言层面的难题。
目前async / await 在 IE edge中已经可以直接使用了，但是chrome和Node.js还没有支持。
幸运的是，babel已经支持async的transform了，所以我们使用的时候引入babel就行。
在开始之前我们需要引入以下的package，preset-stage-3里就有我们需要的async/await的编译文件。

无论是在Browser还是Node.js端都需要安装下面的包。

```
$ npm install babel-core --save
$ npm install babel-preset-es2015 --save
$ npm install babel-preset-stage-3 --save
```
这里推荐使用babel官方提供的require hook方法。就是通过require进来后，
接下来的文件进行require的时候都会经过Babel的处理。因为我们知道CommonJs是同步的模块依赖，
所以也是可行的方法。这个时候，需要编写两个文件，一个是启动的js文件，另外一个是真正执行程序的js文件。

启动文件index.js
```ecmascript 6
require('babel-core/register');
require('./async.js');

```
真正执行程序的async.js
```ecmascript 6
const request = require('request');

const options = {
  url: 'https://api.github.com/repos/cpselvis/zhihu-crawler',
  headers: {
    'User-Agent': 'request'
  }
};

const getRepoData = () => {
  return new Promise((resolve, reject) => {
    request(options, (err, res, body) => {
      if (err) {
        reject(err);
      }
      resolve(body);
    });
  });
};

async function asyncFun() {
 try {
    const value = await getRepoData();
    // ... 和上面的yield类似，如果有多个异步流程，可以放在这里，比如
    // const r1 = await getR1();
    // const r2 = await getR2();
    // const r3 = await getR3();
    // 每个await相当于暂停，执行await之后会等待它后面的函数（不是generator）返回值之后再执行后面其它的await逻辑。
    return value;
  } catch (err) {
    console.log(err);
  }
}

asyncFun().then(x => console.log(`x: ${x}`)).catch(err => console.error(err));

```

注意点：

+ async用来申明里面包裹的内容可以进行同步的方式执行，await则是进行执行顺序控制，
每次执行一个await，程序都会暂停等待await返回值，然后再执行之后的await。
+ await后面调用的函数需要返回一个promise，另外这个函数是一个普通的函数即可，而不是generator。
+ await只能用在async函数之中，用在普通函数中会报错。
+ await命令后面的 Promise 对象，运行结果可能是 rejected，所以最好把 await 命令放在 try...catch 代码块中。
其实，async / await的用法和co差不多，await和yield都是表示暂停，外面包裹一层async 或者 co来表示里面的代码可以采用同步的方式进行处理。不过async / await里面的await后面跟着的函数不需要额外处理，co是需要将它写成一个generator的。

# 2、Vuex 
参考：[Vuex 学习总结](http://www.cnblogs.com/libin-1/p/6518902.html)

简单来说，vuex 就是使用一个 store 对象来包含所有的应用层级状态，也就是数据的来源。
当然如果应用比较庞大，我们可以将 store 模块化，也就是每个模块都有自己的 store。分割方式见如下的代码：
```ecmascript 6
const moduleA={
    state:{...},
    mutations:{...},
    actions:{...},
    getters:{...}
}
const moduleB={
    state:{...},
    mutations:{...},
    actions:{...},
    getters:{...}
}
const store = new Vuex.Store({
    modules:{
        a:moduleA,
        b:moduleB
    }
})
```
从上面的代码我们也可以看出，一个 store 有四个属性：state, getters, mutations, actions。下面我将从这四个属性开始讲。

## 1、State
先来讲state。state 上存放的，说的简单一些就是变量，也就是所谓的状态。
没有使用 state 的时候，我们都是直接在 data 中进行初始化的，但是有了 state 之后，
我们就把 data 上的数据转移到 state 上去了。当一个组件需要获取多个状态时候，
将这些状态都声明为计算属性会有些重复和冗余。为了解决这个问题，
我们可以使用 mapState 辅助函数帮助我们生成计算属性，让你少按几次键：
```ecmascript 6
//在单独构建的版本中辅助函数为Vuex.mapState
import { mapState } from 'vuex'
export  default{
    computed:mapState({
        //箭头函数可使代码更简练
        count:state=>state.count,
        //传统字符串参数‘count’等同于‘state=>state.count’
        countAlias:'count',
        //为了能够使用‘this’获取局部状态，必须使用常规函数
        countPlusLocalState(state){
            return state.count+this.localCount
        }
    })
}
```


其实就是把 state 上保存的变量转移到计算属性上。
当映射的计算属性的名称与 state 的子节点名称相同时，
我们也可以给 mapState 传一个字符串数组。

```ecmascript 6
computed: mapState([
  // 映射 this.count 为 store.state.count
  'count'
])
```
 为了更好地理解这个函数的作用，我们可以看看它的源代码。

可以看到，mapstate 即可以接受对象，也可以接受数组。
最终返回的是一个对象。并且 res[key] 的值都是来于 store 里的，
红色那条代码就是。这样就把两个不相关的属性连接起来了,这也是映射。其他几个辅助函数也是类似的。


## 2、Getters
**getters上简单来说就是存放一些公共函数供组件调用**。
+ getters 会暴露为 store.getters 对象，也就是说可以通过 store.getters[属性]来进行相应的调用。
+ mapGetters 辅助函数仅仅是将 store 中的 getters 映射到局部计算属性，其实也就是从 getters 中获取对应的属性，跟解构类似。
具体如下图 
```ecmascript 6
computed:{
    ...mapGetters([
        'evenOrOdd'
    ])
}
/************************************************************/
const getter = {
    evenOrOdd:state=>state.count %2 == 0 ? 'even':'odd'
}
```
这样我们就可以将 getters 中的 evenOrOdd 属性值传给对应组件中的 evenOrOdd 上。
Getters 接受 state 作为其第一个参数，Getters 也可以接受其他 getters 作为第二个参数。


## 3、Mutations
**mutations 与事件类似，更改 Vuex 的 store 中的状态的唯一方法是提交 mutation。**
所以 mutations 上存放的一般就是我们要改变 state 的一些方法。
```ecmascript 6
const store = new Vuex.Store({
  state: {
    count: 1
  },
  mutations: {
    increment (state) {
      // 变更状态
      state.count++
    }
  }
})
```

我们不能直接调用一个 mutation handler。
这个选项更像是事件注册：“当触发一个类型为 increment 的 mutation 时，调用此函数。”
要唤醒一个 mutation handler，你需要以相应的 type 调用 store.commit 方法：
```ecmascript 6
store.commit('increment')
```
当 mutation 事件类型比较多的时候，我们可以使用常量替代 mutation 事件类型。
同时把这些常量放在单独的文件中可以让我们的代码合作者对整个 app 包含的 mutation 一目了然：
```ecmascript 6
//mutation-types.js
export const SOME_MUTATION = 'SOME_MUTATION'

//store.js
import Vuex from 'vuex'
import { SOME_MUTATION } from './mutation-types'
const store = new Vuex.Store({
    state:{ ... },
    mutations: {
        //我们可以使用ES2015风格的计算属性命名功能来使用一个常量来作为函数名
        [SOME_MUTATION](state){
            //mutate state
        }
    }
})
```
**一条重要的原则就是要记住 mutation 必须是同步函数**。


## 4、Actions
前面说了，mutation 像事件注册，需要相应的触发条件。而 Action 就那个管理触发条件的。
Action 类似于 mutation，不同在于：Action 提交的是 mutation，而不是直接变更状态。Action 可以包含任意异步操作。 

```ecmascript 6
actions: {
    increment (context) {
      context.commit('increment')
    }
  }
```
 

Action 函数接受一个与 store 实例具有相同方法和属性的 context 对象，因此你可以调用 context.commit 
提交一个 mutation，或者通过 context.state 和 context.getters 来获取 state 和 getters。
实践中，我们会经常会用到 ES2015 的 参数解构 来简化代码（特别是我们需要调用 commit 很多次的时候）：

```ecmascript 6
actions: {
  increment ({ commit }) {
    commit('increment')
  }
}
```
 
还记得我们前面说过 mutation 像事件类型吗？因此需要我们给定某个动作来进行触发。
而这就是分发 action。Action 通过 store.dispatch 方法触发：

```ecmascript 6
store.dispatch('increment')
```
 

此外，我们还可以在我们可以在 action 内部执行异步操作：

```ecmascript 6
actions: {
  incrementAsync ({ commit }) {
    setTimeout(() => {
      commit('increment')
    }, 1000)
  }
}
```
 

你在组件中使用 this.$store.dispatch('xxx') 分发 action，
或者使用 mapActions 辅助函数将组件的 methods 映射为 store.dispatch 调用（需要先在根节点注入 store）：

```ecmascript 6
import { mapActions } from 'vuex'

export default {
  // ...
  methods: {
    ...mapActions([
      'increment' // 映射 this.increment() 为 this.$store.dispatch('increment')
    ]),
    ...mapActions({
      add: 'increment' // 映射 this.add() 为 this.$store.dispatch('increment')
    })
  }
}
``` 

这句话意思其实是，当你使用了 mapActions， 你就不需要再次使用 this.$store.dispatch('xxx')，
当你没使用的话，你可以需要手动去分法。比如下面的代码：

```ecmascript 6
mothods:{
    save(){
        const plan = {
            date:this.date,
            totalTime:this.totalTime,
            comment: this.comment
        }
        this.$store.dispatch('savePlan',plan)
        this.$store.dispatch('addTotalTime',tis.totalTime)
        this.$router.go(-1)
    }
}
```

什么时候用this.$store.dispatch('xxx')，什么时候用 mapActions 大家要根据情况而定的。
 

最后，问大家一个问题，你知道什么时候有扩展符 (...) 吗? 不知道你有没有注意，
有些有扩展符，有些没有。

> **提示：有扩展符的，都是被包含在一个对象里了。**