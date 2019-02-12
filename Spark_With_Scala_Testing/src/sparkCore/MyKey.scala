package sparkCore

class MyKey(val first: Int, val second: Int) extends Ordered[MyKey] with Serializable {
  def compare(that:MyKey): Int = {
    //第一列升序，第二列升序
    /*if(first - that.first==0){
      second - that.second
    }else {
      first - that.first
    }*/
    //第一列降序，第二列升序
    if(first - that.first==0){
      second - that.second
    }else {
      that.first - first
    }
    //参照MapReduce二次排序原理：https://github.com/josonle/MapReduce-Demo#%E8%A7%A3%E7%AD%94%E6%80%9D%E8%B7%AF-1
  }
}