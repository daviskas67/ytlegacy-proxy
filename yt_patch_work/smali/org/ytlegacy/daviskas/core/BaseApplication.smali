.class public abstract Lorg/ytlegacy/daviskas/core/BaseApplication;
.super Landroid/app/Application;
.source "SourceFile"

# interfaces
.implements Lorg/ytlegacy/daviskas/core/a;
.implements Lorg/ytlegacy/daviskas/core/async/bw;
.implements Lorg/ytlegacy/daviskas/core/async/f;
.implements Lorg/ytlegacy/daviskas/core/f;
.implements Lorg/ytlegacy/daviskas/core/player/bk;
.implements Lorg/ytlegacy/daviskas/core/utils/r;


# instance fields
.field private a:Lorg/ytlegacy/daviskas/core/utils/aj;

.field private b:Landroid/os/Handler;

.field private c:Ljava/util/concurrent/Executor;

.field private d:Ljava/util/concurrent/Executor;

.field private e:Ljava/util/concurrent/Executor;

.field private f:Lorg/apache/http/impl/client/AbstractHttpClient;

.field private g:Lorg/apache/http/impl/client/AbstractHttpClient;

.field private h:Lorg/ytlegacy/daviskas/core/converter/m;

.field private i:Landroid/content/SharedPreferences;

.field private j:Lorg/ytlegacy/daviskas/core/player/bi;

.field private k:Lorg/ytlegacy/daviskas/core/utils/q;

.field private l:Lorg/ytlegacy/daviskas/core/e;

.field private m:Lorg/ytlegacy/daviskas/core/async/a;

.field private n:Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

.field private o:Lorg/ytlegacy/daviskas/core/Analytics;

.field private p:Lorg/ytlegacy/daviskas/core/utils/SafeSearch;

.field private q:Z

.field private r:Z

.field private s:Ljava/lang/String;

.field private t:Lorg/ytlegacy/daviskas/core/player/a/h;


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 49
    invoke-direct {p0}, Landroid/app/Application;-><init>()V

    return-void
.end method

.method static synthetic a(Lorg/ytlegacy/daviskas/core/BaseApplication;)Landroid/os/Handler;
    .locals 1

    .prologue
    .line 49
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->b:Landroid/os/Handler;

    return-object v0
.end method

.method private static a(III)Ljava/util/concurrent/Executor;
    .locals 8

    .prologue
    .line 363
    new-instance v0, Ljava/util/concurrent/ThreadPoolExecutor;

    const-wide/16 v3, 0x3c

    sget-object v5, Ljava/util/concurrent/TimeUnit;->SECONDS:Ljava/util/concurrent/TimeUnit;

    new-instance v6, Ljava/util/concurrent/LinkedBlockingQueue;

    invoke-direct {v6}, Ljava/util/concurrent/LinkedBlockingQueue;-><init>()V

    new-instance v7, Lorg/ytlegacy/daviskas/core/utils/z;

    const/16 v1, 0xa

    invoke-direct {v7, v1}, Lorg/ytlegacy/daviskas/core/utils/z;-><init>(I)V

    move v1, p0

    move v2, p0

    invoke-direct/range {v0 .. v7}, Ljava/util/concurrent/ThreadPoolExecutor;-><init>(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;Ljava/util/concurrent/ThreadFactory;)V

    return-object v0
.end method


# virtual methods
.method public final L()Lorg/ytlegacy/daviskas/core/utils/aj;
    .locals 1

    .prologue
    .line 204
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->a:Lorg/ytlegacy/daviskas/core/utils/aj;

    return-object v0
.end method

.method public final M()Ljava/util/concurrent/Executor;
    .locals 1

    .prologue
    .line 212
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->c:Ljava/util/concurrent/Executor;

    return-object v0
.end method

.method public final N()Ljava/util/concurrent/Executor;
    .locals 1

    .prologue
    .line 216
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->d:Ljava/util/concurrent/Executor;

    return-object v0
.end method

.method public final O()Ljava/util/concurrent/Executor;
    .locals 1

    .prologue
    .line 220
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->e:Ljava/util/concurrent/Executor;

    return-object v0
.end method

.method public final P()Lorg/apache/http/client/HttpClient;
    .locals 1

    .prologue
    .line 224
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->f:Lorg/apache/http/impl/client/AbstractHttpClient;

    return-object v0
.end method

.method public final Q()Lorg/apache/http/impl/client/AbstractHttpClient;
    .locals 1

    .prologue
    .line 232
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->g:Lorg/apache/http/impl/client/AbstractHttpClient;

    return-object v0
.end method

.method public final R()Lorg/ytlegacy/daviskas/core/converter/m;
    .locals 1

    .prologue
    .line 236
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->h:Lorg/ytlegacy/daviskas/core/converter/m;

    return-object v0
.end method

.method public final S()Landroid/content/SharedPreferences;
    .locals 1

    .prologue
    .line 245
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->i:Landroid/content/SharedPreferences;

    return-object v0
.end method

.method public final T()Lorg/ytlegacy/daviskas/core/async/a;
    .locals 1

    .prologue
    .line 250
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->m:Lorg/ytlegacy/daviskas/core/async/a;

    return-object v0
.end method

.method public final U()Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;
    .locals 1

    .prologue
    .line 255
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->n:Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

    return-object v0
.end method

.method public final V()Lorg/ytlegacy/daviskas/core/utils/SafeSearch;
    .locals 1

    .prologue
    .line 259
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->p:Lorg/ytlegacy/daviskas/core/utils/SafeSearch;

    return-object v0
.end method

.method public final W()Z
    .locals 1

    .prologue
    .line 273
    iget-boolean v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->q:Z

    return v0
.end method

.method public final X()Ljava/lang/String;
    .locals 1

    .prologue
    .line 303
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->s:Ljava/lang/String;

    return-object v0
.end method

.method public final Y()Lorg/ytlegacy/daviskas/core/player/a/h;
    .locals 2

    .prologue
    .line 374
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->t:Lorg/ytlegacy/daviskas/core/player/a/h;

    if-nez v0, :cond_0

    .line 375
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->i:Landroid/content/SharedPreferences;

    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/utils/h;->a(Landroid/content/SharedPreferences;)Ljava/security/Key;

    move-result-object v0

    .line 377
    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->s:Ljava/lang/String;

    invoke-static {p0, v0, v1}, Lorg/ytlegacy/daviskas/core/player/a/h;->a(Landroid/content/Context;Ljava/security/Key;Ljava/lang/String;)Lorg/ytlegacy/daviskas/core/player/a/h;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->t:Lorg/ytlegacy/daviskas/core/player/a/h;

    .line 379
    :cond_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->t:Lorg/ytlegacy/daviskas/core/player/a/h;

    return-object v0
.end method

.method protected a()Z
    .locals 1

    .prologue
    .line 104
    const/4 v0, 0x1

    return v0
.end method

.method public abstract c()Lorg/ytlegacy/daviskas/core/c;
.end method

.method protected abstract d()Lorg/ytlegacy/daviskas/core/async/a;
.end method

.method protected e_()V
    .locals 9

    .prologue
    const/16 v6, 0x3c

    const/16 v5, 0xa

    const/4 v1, 0x1

    const/4 v4, 0x0

    .line 119
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->p()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/L;->a(Ljava/lang/String;)V

    .line 121
    const-string v0, "youtube"

    invoke-virtual {p0, v0, v4}, Lorg/ytlegacy/daviskas/core/BaseApplication;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->i:Landroid/content/SharedPreferences;

    .line 122
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->i:Landroid/content/SharedPreferences;

    const-string v2, "version"

    const/4 v3, 0x0

    invoke-interface {v0, v2, v3}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 123
    invoke-static {p0}, Lorg/ytlegacy/daviskas/core/utils/s;->d(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v2

    .line 124
    invoke-virtual {v2, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_2

    move v0, v1

    :goto_0
    iput-boolean v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->q:Z

    .line 125
    iget-boolean v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->q:Z

    if-eqz v0, :cond_0

    .line 126
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->i:Landroid/content/SharedPreferences;

    invoke-interface {v0}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    const-string v3, "version"

    invoke-interface {v0, v3, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    const-string v2, "device_id"

    invoke-interface {v0, v2}, Landroid/content/SharedPreferences$Editor;->remove(Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    const-string v2, "device_key"

    invoke-interface {v0, v2}, Landroid/content/SharedPreferences$Editor;->remove(Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/utils/Util;->a(Landroid/content/SharedPreferences$Editor;)V

    .line 132
    :cond_0
    new-instance v0, Lorg/ytlegacy/daviskas/core/utils/aj;

    invoke-direct {v0}, Lorg/ytlegacy/daviskas/core/utils/aj;-><init>()V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->a:Lorg/ytlegacy/daviskas/core/utils/aj;

    .line 133
    new-instance v0, Landroid/os/Handler;

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->getMainLooper()Landroid/os/Looper;

    move-result-object v2

    invoke-direct {v0, v2}, Landroid/os/Handler;-><init>(Landroid/os/Looper;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->b:Landroid/os/Handler;

    .line 134
    new-instance v0, Lorg/ytlegacy/daviskas/core/b;

    invoke-direct {v0, p0}, Lorg/ytlegacy/daviskas/core/b;-><init>(Lorg/ytlegacy/daviskas/core/BaseApplication;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->c:Ljava/util/concurrent/Executor;

    .line 135
    const/16 v0, 0x10

    invoke-static {v0, v6, v5}, Lorg/ytlegacy/daviskas/core/BaseApplication;->a(III)Ljava/util/concurrent/Executor;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->d:Ljava/util/concurrent/Executor;

    .line 136
    invoke-static {}, Ljava/lang/Runtime;->getRuntime()Ljava/lang/Runtime;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/Runtime;->availableProcessors()I

    move-result v0

    add-int/lit8 v0, v0, -0x1

    invoke-static {v1, v0}, Ljava/lang/Math;->max(II)I

    move-result v0

    invoke-static {v0, v6, v5}, Lorg/ytlegacy/daviskas/core/BaseApplication;->a(III)Ljava/util/concurrent/Executor;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->e:Ljava/util/concurrent/Executor;

    .line 137
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->j()Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->s:Ljava/lang/String;

    .line 138
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->s:Ljava/lang/String;

    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/utils/at;->a(Ljava/lang/String;)Lorg/apache/http/impl/client/AbstractHttpClient;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->f:Lorg/apache/http/impl/client/AbstractHttpClient;

    .line 139
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->s:Ljava/lang/String;

    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/utils/at;->b(Ljava/lang/String;)Lorg/apache/http/impl/client/AbstractHttpClient;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->g:Lorg/apache/http/impl/client/AbstractHttpClient;

    .line 140
    invoke-static {}, Lorg/ytlegacy/daviskas/core/converter/m;->a()Lorg/ytlegacy/daviskas/core/converter/m;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->h:Lorg/ytlegacy/daviskas/core/converter/m;

    .line 142
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->d()Lorg/ytlegacy/daviskas/core/async/a;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->m:Lorg/ytlegacy/daviskas/core/async/a;

    .line 143
    new-instance v0, Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->m:Lorg/ytlegacy/daviskas/core/async/a;

    iget-object v2, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->i:Landroid/content/SharedPreferences;

    sget v3, Lorg/ytlegacy/daviskas/core/utils/Util;->a:I

    const/16 v5, 0xe

    if-lt v3, v5, :cond_1

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->getApplicationInfo()Landroid/content/pm/ApplicationInfo;

    move-result-object v3

    iget v3, v3, Landroid/content/pm/ApplicationInfo;->targetSdkVersion:I

    const/16 v5, 0xb

    if-ge v3, v5, :cond_3

    :cond_1
    new-instance v3, Lorg/ytlegacy/daviskas/core/async/ai;

    invoke-direct {v3}, Lorg/ytlegacy/daviskas/core/async/ai;-><init>()V

    :goto_1
    iget-object v5, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->a:Lorg/ytlegacy/daviskas/core/utils/aj;

    invoke-direct/range {v0 .. v5}, Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;-><init>(Lorg/ytlegacy/daviskas/core/async/a;Landroid/content/SharedPreferences;Lorg/ytlegacy/daviskas/core/async/bx;ZLorg/ytlegacy/daviskas/core/utils/f;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->n:Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

    .line 146
    new-instance v0, Lorg/ytlegacy/daviskas/core/utils/SafeSearch;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->i:Landroid/content/SharedPreferences;

    invoke-direct {v0, p0, v1}, Lorg/ytlegacy/daviskas/core/utils/SafeSearch;-><init>(Landroid/content/Context;Landroid/content/SharedPreferences;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->p:Lorg/ytlegacy/daviskas/core/utils/SafeSearch;

    .line 147
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->d:Ljava/util/concurrent/Executor;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->p:Lorg/ytlegacy/daviskas/core/utils/SafeSearch;

    invoke-interface {v0, v1}, Ljava/util/concurrent/Executor;->execute(Ljava/lang/Runnable;)V

    .line 150
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->c()Lorg/ytlegacy/daviskas/core/c;

    move-result-object v1

    .line 151
    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/c;->y()Z

    move-result v0

    if-eqz v0, :cond_5

    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/c;->h()Ljava/lang/String;

    move-result-object v0

    if-eqz v0, :cond_5

    .line 152
    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/c;->D()Ljava/lang/String;

    move-result-object v0

    .line 153
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    invoke-static {p0}, Lorg/ytlegacy/daviskas/core/utils/s;->d(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    move-result v3

    if-eqz v3, :cond_4

    const-string v0, ""

    :goto_2
    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    .line 155
    new-instance v0, Lorg/ytlegacy/daviskas/core/client/p;

    iget-object v2, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->d:Ljava/util/concurrent/Executor;

    invoke-static {}, Lorg/ytlegacy/daviskas/core/utils/Util;->a()Ljava/lang/String;

    move-result-object v3

    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/c;->h()Ljava/lang/String;

    move-result-object v5

    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/c;->z()I

    move-result v6

    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/c;->A()I

    move-result v7

    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/c;->B()Ljava/lang/String;

    move-result-object v8

    move-object v1, p0

    invoke-direct/range {v0 .. v8}, Lorg/ytlegacy/daviskas/core/client/p;-><init>(Landroid/content/Context;Ljava/util/concurrent/Executor;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;)V

    move-object v1, v0

    .line 168
    :goto_3
    new-instance v2, Lorg/ytlegacy/daviskas/core/utils/i;

    const-string v0, "connectivity"

    invoke-virtual {p0, v0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/net/ConnectivityManager;

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->c()Lorg/ytlegacy/daviskas/core/c;

    move-result-object v3

    invoke-direct {v2, v0, v3}, Lorg/ytlegacy/daviskas/core/utils/i;-><init>(Landroid/net/ConnectivityManager;Lorg/ytlegacy/daviskas/core/c;)V

    iput-object v2, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->k:Lorg/ytlegacy/daviskas/core/utils/q;

    .line 172
    new-instance v0, Lorg/ytlegacy/daviskas/core/d;

    iget-object v2, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->k:Lorg/ytlegacy/daviskas/core/utils/q;

    invoke-direct {v0, v1, v2}, Lorg/ytlegacy/daviskas/core/d;-><init>(Lorg/ytlegacy/daviskas/core/client/b;Lorg/ytlegacy/daviskas/core/utils/q;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->o:Lorg/ytlegacy/daviskas/core/Analytics;

    .line 174
    new-instance v0, Lorg/ytlegacy/daviskas/core/e;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->k:Lorg/ytlegacy/daviskas/core/utils/q;

    invoke-direct {v0, p0, v1}, Lorg/ytlegacy/daviskas/core/e;-><init>(Landroid/content/Context;Lorg/ytlegacy/daviskas/core/utils/q;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->l:Lorg/ytlegacy/daviskas/core/e;

    .line 175
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->l()Lorg/ytlegacy/daviskas/core/player/bi;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->j:Lorg/ytlegacy/daviskas/core/player/bi;

    .line 176
    return-void

    :cond_2
    move v0, v4

    .line 124
    goto/16 :goto_0

    .line 143
    :cond_3
    new-instance v3, Lorg/ytlegacy/daviskas/core/v14/a;

    invoke-direct {v3}, Lorg/ytlegacy/daviskas/core/v14/a;-><init>()V

    goto/16 :goto_1

    .line 153
    :cond_4
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, " ["

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v3, "]"

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    goto :goto_2

    .line 165
    :cond_5
    new-instance v0, Lorg/ytlegacy/daviskas/core/client/ay;

    invoke-direct {v0}, Lorg/ytlegacy/daviskas/core/client/ay;-><init>()V

    move-object v1, v0

    goto :goto_3
.end method

.method public final g()Lorg/ytlegacy/daviskas/core/player/bi;
    .locals 1

    .prologue
    .line 264
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->j:Lorg/ytlegacy/daviskas/core/player/bi;

    return-object v0
.end method

.method public final h()Lorg/ytlegacy/daviskas/core/Analytics;
    .locals 1

    .prologue
    .line 312
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->o:Lorg/ytlegacy/daviskas/core/Analytics;

    return-object v0
.end method

.method public final i()Lorg/ytlegacy/daviskas/core/e;
    .locals 1

    .prologue
    .line 241
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->l:Lorg/ytlegacy/daviskas/core/e;

    return-object v0
.end method

.method protected j()Ljava/lang/String;
    .locals 2

    .prologue
    .line 179
    invoke-static {p0}, Lorg/ytlegacy/daviskas/core/utils/s;->d(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->c()Lorg/ytlegacy/daviskas/core/c;

    move-result-object v1

    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/c;->D()Ljava/lang/String;

    move-result-object v1

    invoke-static {p0, v0, v1}, Lorg/ytlegacy/daviskas/core/utils/Util;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public final k()Lorg/ytlegacy/daviskas/core/utils/q;
    .locals 1

    .prologue
    .line 269
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->k:Lorg/ytlegacy/daviskas/core/utils/q;

    return-object v0
.end method

.method protected l()Lorg/ytlegacy/daviskas/core/player/bi;
    .locals 9

    .prologue
    const/4 v4, 0x1

    const/4 v5, 0x0

    .line 184
    new-instance v0, Lorg/ytlegacy/daviskas/core/player/ad;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->k:Lorg/ytlegacy/daviskas/core/utils/q;

    invoke-static {p0}, Lorg/ytlegacy/daviskas/core/utils/k;->c(Landroid/content/Context;)Z

    move-result v2

    if-eqz v2, :cond_0

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->c()Lorg/ytlegacy/daviskas/core/c;

    move-result-object v2

    invoke-interface {v2}, Lorg/ytlegacy/daviskas/core/c;->H()Z

    move-result v2

    if-eqz v2, :cond_0

    move v2, v4

    :goto_0
    invoke-static {p0}, Lorg/ytlegacy/daviskas/core/utils/o;->a(Landroid/content/Context;)Z

    move-result v3

    invoke-virtual {p0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v6

    const-string v7, "youtube:device_lowend"

    const/4 v8, 0x2

    invoke-static {v6, v7, v8}, Lcom/google/android/gsf/f;->a(Landroid/content/ContentResolver;Ljava/lang/String;I)I

    move-result v6

    packed-switch v6, :pswitch_data_0

    invoke-static {p0}, Lorg/ytlegacy/daviskas/core/utils/Util;->d(Landroid/content/Context;)Z

    move-result v4

    :goto_1
    :pswitch_0
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->c()Lorg/ytlegacy/daviskas/core/c;

    move-result-object v5

    invoke-interface {v5}, Lorg/ytlegacy/daviskas/core/c;->E()Z

    move-result v5

    invoke-direct/range {v0 .. v5}, Lorg/ytlegacy/daviskas/core/player/ad;-><init>(Lorg/ytlegacy/daviskas/core/utils/q;ZZZZ)V

    return-object v0

    :cond_0
    move v2, v5

    goto :goto_0

    :pswitch_1
    move v4, v5

    goto :goto_1

    nop

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method public n()Ljava/lang/String;
    .locals 1

    .prologue
    .line 370
    const/4 v0, 0x0

    return-object v0
.end method

.method public final onCreate()V
    .locals 1
    new-instance v0, Landroid/os/StrictMode$ThreadPolicy$Builder;
    invoke-direct {v0}, Landroid/os/StrictMode$ThreadPolicy$Builder;-><init>()V
    invoke-virtual {v0}, Landroid/os/StrictMode$ThreadPolicy$Builder;->permitAll()Landroid/os/StrictMode$ThreadPolicy$Builder;
    move-result-object v0
    invoke-virtual {v0}, Landroid/os/StrictMode$ThreadPolicy$Builder;->build()Landroid/os/StrictMode$ThreadPolicy;
    move-result-object v0
    invoke-static {v0}, Landroid/os/StrictMode;->setThreadPolicy(Landroid/os/StrictMode$ThreadPolicy;)V


    .prologue
    .line 92
    invoke-super {p0}, Landroid/app/Application;->onCreate()V

    .line 93
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->a()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 94
    iget-boolean v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->r:Z

    if-nez v0, :cond_0

    const/4 v0, 0x1

    iput-boolean v0, p0, Lorg/ytlegacy/daviskas/core/BaseApplication;->r:Z

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/BaseApplication;->e_()V

    .line 96
    :cond_0
    return-void
.end method

.method public p()Ljava/lang/String;
    .locals 1

    .prologue
    .line 299
    const-string v0, "?"

    return-object v0
.end method
