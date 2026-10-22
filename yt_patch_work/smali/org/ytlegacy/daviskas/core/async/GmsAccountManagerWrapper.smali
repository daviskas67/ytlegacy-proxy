.class public final Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;
.super Lorg/ytlegacy/daviskas/core/async/a;
.source "SourceFile"


# instance fields
.field private final d:Landroid/content/Context;

.field private final e:Ljava/util/concurrent/Executor;

.field private f:Lorg/ytlegacy/daviskas/core/async/an;


# direct methods
.method public constructor <init>(Landroid/content/Context;Ljava/lang/String;Ljava/util/concurrent/Executor;)V
    .locals 2

    .prologue
    .line 51
    invoke-static {p1}, Landroid/accounts/AccountManager;->get(Landroid/content/Context;)Landroid/accounts/AccountManager;

    move-result-object v0

    const-string v1, "com.google"

    invoke-direct {p0, v0, p2, v1}, Lorg/ytlegacy/daviskas/core/async/a;-><init>(Landroid/accounts/AccountManager;Ljava/lang/String;Ljava/lang/String;)V

    .line 52
    const-string v0, "applicationContext cannot be null"

    invoke-static {p1, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/content/Context;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->d:Landroid/content/Context;

    .line 54
    const-string v0, "executor cannot be null"

    invoke-static {p3, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/util/concurrent/Executor;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->e:Ljava/util/concurrent/Executor;

    .line 56
    return-void
.end method

.method static synthetic a(Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;)Landroid/content/Context;
    .locals 1

    .prologue
    .line 43
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->d:Landroid/content/Context;

    return-object v0
.end method

.method private a(Landroid/support/v4/app/FragmentActivity;Landroid/accounts/Account;Lorg/ytlegacy/daviskas/core/async/bp;Z)V
    .locals 7

    .prologue
    .line 149
    iget-object v6, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->e:Ljava/util/concurrent/Executor;

    new-instance v0, Lorg/ytlegacy/daviskas/core/async/am;

    move-object v1, p0

    move-object v2, p1

    move-object v3, p2

    move-object v4, p3

    move v5, p4

    invoke-direct/range {v0 .. v5}, Lorg/ytlegacy/daviskas/core/async/am;-><init>(Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;Landroid/support/v4/app/FragmentActivity;Landroid/accounts/Account;Lorg/ytlegacy/daviskas/core/async/bp;Z)V

    invoke-interface {v6, v0}, Ljava/util/concurrent/Executor;->execute(Ljava/lang/Runnable;)V

    .line 155
    return-void
.end method

.method static synthetic a(Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;Lorg/ytlegacy/daviskas/core/async/an;)V
    .locals 1

    .prologue
    .line 43
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->f:Lorg/ytlegacy/daviskas/core/async/an;

    iput-object p1, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->f:Lorg/ytlegacy/daviskas/core/async/an;

    if-eqz v0, :cond_0

    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/async/an;->a(Lorg/ytlegacy/daviskas/core/async/an;)Lorg/ytlegacy/daviskas/core/async/bp;

    move-result-object v0

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/async/bp;->d()V

    :cond_0
    return-void
.end method


# virtual methods
.method protected final a(Landroid/accounts/Account;)Lorg/ytlegacy/daviskas/core/model/UserAuth;
    .locals 4

    .prologue
    const/4 v0, 0x0

    .line 105
    :try_start_0
    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->d:Landroid/content/Context;

    iget-object v2, p1, Landroid/accounts/Account;->name:Ljava/lang/String;

    iget-object v3, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->c:Ljava/lang/String;

    invoke-static {v1, v2, v3}, Lcom/google/android/gms/auth/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Lcom/google/android/gms/auth/GoogleAuthException; {:try_start_0 .. :try_end_0} :catch_1
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_2

    move-result-object v1

    .line 116
    if-eqz v1, :cond_0

    .line 117
    new-instance v0, Lorg/ytlegacy/daviskas/core/model/UserAuth;

    iget-object v2, p1, Landroid/accounts/Account;->name:Ljava/lang/String;

    invoke-direct {v0, v2, v1}, Lorg/ytlegacy/daviskas/core/model/UserAuth;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    .line 121
    :goto_0
    return-object v0

    .line 106
    :catch_0
    move-exception v1

    .line 107
    const-string v2, "Cannot get user auth; network error"

    invoke-static {v2, v1}, Lorg/ytlegacy/daviskas/core/L;->a(Ljava/lang/String;Ljava/lang/Throwable;)V

    goto :goto_0

    .line 109
    :catch_1
    move-exception v1

    .line 110
    const-string v2, "Cannot get user auth"

    invoke-static {v2, v1}, Lorg/ytlegacy/daviskas/core/L;->a(Ljava/lang/String;Ljava/lang/Throwable;)V

    goto :goto_0

    .line 112
    :catch_2
    move-exception v1

    .line 113
    const-string v2, "Cannot get user auth"

    invoke-static {v2, v1}, Lorg/ytlegacy/daviskas/core/L;->a(Ljava/lang/String;Ljava/lang/Throwable;)V

    goto :goto_0

    .line 120
    :cond_0
    const-string v1, "got null authToken for the selected account"

    invoke-static {v1}, Lorg/ytlegacy/daviskas/core/L;->b(Ljava/lang/String;)V

    goto :goto_0
.end method

.method protected final a(Landroid/accounts/Account;Landroid/app/Activity;Lorg/ytlegacy/daviskas/core/async/bp;)V
    .locals 2

    .prologue
    .line 67
    instance-of v0, p2, Landroid/support/v4/app/FragmentActivity;

    const-string v1, "activity must be a FragmentActivity"

    invoke-static {v0, v1}, Lorg/ytlegacy/daviskas/core/utils/u;->a(ZLjava/lang/Object;)V

    .line 69
    check-cast p2, Landroid/support/v4/app/FragmentActivity;

    const/4 v0, 0x0

    invoke-direct {p0, p2, p1, p3, v0}, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->a(Landroid/support/v4/app/FragmentActivity;Landroid/accounts/Account;Lorg/ytlegacy/daviskas/core/async/bp;Z)V

    .line 70
    return-void
.end method

.method public final a(Landroid/accounts/Account;Landroid/support/v4/app/FragmentActivity;Lorg/ytlegacy/daviskas/core/async/bp;Z)V
    .locals 1

    .prologue
    .line 81
    const-string v0, "account cannot be null"

    invoke-static {p1, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 82
    const-string v0, "activity cannot be null"

    invoke-static {p2, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 83
    const-string v0, "callback cannot be null"

    invoke-static {p3, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 84
    invoke-direct {p0, p2, p1, p3, p4}, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->a(Landroid/support/v4/app/FragmentActivity;Landroid/accounts/Account;Lorg/ytlegacy/daviskas/core/async/bp;Z)V

    .line 85
    return-void
.end method

.method protected final a(Landroid/accounts/Account;Lorg/ytlegacy/daviskas/core/async/bp;)V
    .locals 2

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->e:Ljava/util/concurrent/Executor;

    new-instance v1, Lorg/ytlegacy/daviskas/core/async/al;

    invoke-direct {v1, p0, p1, p2}, Lorg/ytlegacy/daviskas/core/async/al;-><init>(Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;Landroid/accounts/Account;Lorg/ytlegacy/daviskas/core/async/bp;)V

    invoke-interface {v0, v1}, Ljava/util/concurrent/Executor;->execute(Ljava/lang/Runnable;)V

    .line 61
    return-void
.end method

.method public final a(Lorg/ytlegacy/daviskas/core/model/UserAuth;)V
    .locals 2

    .prologue
    .line 126
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->d:Landroid/content/Context;

    iget-object v1, p1, Lorg/ytlegacy/daviskas/core/model/UserAuth;->authToken:Ljava/lang/String;

    invoke-static {v0, v1}, Lcom/google/android/gms/auth/a;->a(Landroid/content/Context;Ljava/lang/String;)V

    .line 127
    return-void
.end method

.method public final a(IILandroid/content/Intent;)Z
    .locals 3

    .prologue
    const/4 v0, 0x0

    .line 89
    const/16 v1, 0x388

    if-eq p1, v1, :cond_1

    .line 98
    :cond_0
    :goto_0
    return v0

    .line 92
    :cond_1
    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->f:Lorg/ytlegacy/daviskas/core/async/an;

    .line 93
    const/4 v2, 0x0

    iput-object v2, p0, Lorg/ytlegacy/daviskas/core/async/GmsAccountManagerWrapper;->f:Lorg/ytlegacy/daviskas/core/async/an;

    .line 94
    if-eqz v1, :cond_0

    .line 97
    invoke-virtual {v1, p2}, Lorg/ytlegacy/daviskas/core/async/an;->a(I)V

    .line 98
    const/4 v0, 0x1

    goto :goto_0
.end method
