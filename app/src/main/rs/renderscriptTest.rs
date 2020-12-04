#pragma version(1)
#pragma rs java_package_name(com.sonoptek.renderscriptdemo)


typedef struct Point {
        float2 position;
        float size;
    } Point_t;

uint16_t color;
uchar4 RS_KERNEL invert(uchar4 in, uint32_t x, uint32_t y) {
      uchar4 out = in;
      out.r = color - in.r;
      out.g = color - in.g;
      out.b = color - in.b;
      return out;
}

#pragma rs reduce(addint) accumulator(addintAccum)
    static void addintAccum(int *accum, int val) {
       // rsDebug("======my renderscript debug========",rsUptimeMillis());//打印日志
        *accum += val;
    }

#define LONG_MAX (long)((1UL<<63)-1)
#define LONG_MIN (long)(1UL<<63)

typedef struct IndexedVal{
    long val;
    int idx;
}IndexedVal;
typedef struct MinAndMaxJava{
    IndexedVal min,max;
}MinAndMax;

#pragma rs reduce(findMinAndMax)\
initializer(fMMInit) accumulator(fMMAccumulator)\
combiner(fMMCombiner) outconverter(fMMOutConverter)

static void fMMInit(MinAndMax *accum){
    accum->min.val= LONG_MAX;
    accum->min.idx=-1;
    accum->max.val=LONG_MIN;
    accum->max.idx=-1;
}

static void fMMAccumulator(MinAndMax *accum,long in,int x){
    IndexedVal me;
    me.val=in;
    me.idx=x;
     if(me.val<=accum->min.val){
        accum->min=me;
     }
     if(me.val>=accum->max.val){
        accum->max=me;
     }
     //rsDebug("======my renderscript debug=====",in);//打印日志
}

static void fMMCombiner(MinAndMax *accum,const MinAndMax *val){
    if((accum->min.idx)<0||(val->min.val<accum->min.val)){
        accum->min=val->min;
    }
    if((accum->max.idx)<0||(val->max.val>accum->max.val)){
        accum->max=val->max;
    }
}
static void fMMOutConverter(int2 *result,const MinAndMax *val){
    result->x=val->min.idx;
    result->y=val->max.idx;
}

#pragma rs reduce(findMax) accumulator(findMaxAccum)
static void findMaxAccum(int* accum,int val){
    if(*accum <val){
        *accum=val;
    }
}

uchar RS_KERNEL I32toU8(int in,uint32_t x,uint32_t y){
    int out=in>255? 255:in;
    out=out<0? 0:out;
    return (uchar) out;
}