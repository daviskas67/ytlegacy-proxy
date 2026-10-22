.class abstract Lorg/ytlegacy/daviskas/app/honeycomb/o;
.super Landroid/app/Activity;
.source "SourceFile"


# direct methods
.method private constructor <init>()V
    .locals 0

    .prologue
    .line 61
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V

    return-void
.end method

.method synthetic constructor <init>(B)V
    .locals 0

    .prologue
    .line 61
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;-><init>()V

    return-void
.end method

.method private e()Landroid/content/Intent;
    .locals 3

    .prologue
    .line 97
    new-instance v1, Landroid/content/Intent;

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->getIntent()Landroid/content/Intent;

    move-result-object v0

    invoke-direct {v1, v0}, Landroid/content/Intent;-><init>(Landroid/content/Intent;)V

    .line 98
    invoke-static {p0}, Lorg/ytlegacy/daviskas/core/utils/k;->b(Landroid/content/Context;)Z

    move-result v0

    if-eqz v0, :cond_0

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->a()Ljava/lang/Class;

    move-result-object v0

    .line 100
    :goto_0
    new-instance v2, Landroid/content/ComponentName;

    invoke-direct {v2, p0, v0}, Landroid/content/ComponentName;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    invoke-virtual {v1, v2}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    .line 101
    const-string v0, "alias"

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->getIntent()Landroid/content/Intent;

    move-result-object v2

    invoke-virtual {v2}, Landroid/content/Intent;->getComponent()Landroid/content/ComponentName;

    move-result-object v2

    invoke-virtual {v2}, Landroid/content/ComponentName;->getClassName()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v0, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    .line 103
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->c()I

    move-result v0

    invoke-virtual {v1, v0}, Landroid/content/Intent;->setFlags(I)Landroid/content/Intent;

    .line 104
    return-object v1

    .line 98
    :cond_0
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->b()Ljava/lang/Class;

    move-result-object v0

    goto :goto_0
.end method


# virtual methods
.method protected a()Ljava/lang/Class;
    .locals 1

    .prologue
    .line 108
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->b()Ljava/lang/Class;

    move-result-object v0

    return-object v0
.end method

.method protected abstract b()Ljava/lang/Class;
.end method

.method protected c()I
    .locals 1

    .prologue
    .line 114
    const/high16 v0, 0x20000000

    return v0
.end method

.method protected d()Z
    .locals 1

    .prologue
    .line 122
    const/4 v0, 0x0

    return v0
.end method

.method public onCreate(Landroid/os/Bundle;)V
    .locals 6

    .prologue
    .line 64
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    .line 65
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->getApplication()Landroid/app/Application;

    move-result-object v0

    check-cast v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;

    .line 66
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->S()Landroid/content/SharedPreferences;

    move-result-object v1

    .line 67
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->o()Lorg/ytlegacy/daviskas/app/s;

    move-result-object v2

    .line 69
    invoke-static {v0, v2, v1}, Lorg/ytlegacy/daviskas/app/honeycomb/Shell;->a(Lorg/ytlegacy/daviskas/app/YouTubeApplication;Lorg/ytlegacy/daviskas/app/s;Landroid/content/SharedPreferences;)V

    .line 70
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v2

    invoke-static {}, Lorg/ytlegacy/daviskas/app/honeycomb/Shell;->a()J

    move-result-wide v4

    sub-long/2addr v2, v4

    .line 71
    invoke-static {}, Lorg/ytlegacy/daviskas/app/honeycomb/Shell;->b()Z

    move-result v0

    if-nez v0, :cond_0

    invoke-static {}, Lorg/ytlegacy/daviskas/app/honeycomb/Shell;->c()Z

    move-result v0

    if-eqz v0, :cond_2

    invoke-static {}, Lorg/ytlegacy/daviskas/app/honeycomb/Shell;->d()J

    move-result-wide v4

    cmp-long v0, v2, v4

    if-lez v0, :cond_2

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->d()Z

    move-result v0

    if-eqz v0, :cond_2

    :cond_0
    const/4 v0, 0x1

    .line 75
    :goto_0
    # PATCH: Всегда прыгаем мимо экрана обновления
    goto :cond_3

    .line 76
    new-instance v0, Landroid/content/Intent;

    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->getIntent()Landroid/content/Intent;

    move-result-object v2

    invoke-direct {v0, v2}, Landroid/content/Intent;-><init>(Landroid/content/Intent;)V

    .line 77
    new-instance v2, Landroid/content/ComponentName;

    const-class v3, Lorg/ytlegacy/daviskas/app/honeycomb/phone/NewVersionAvailableActivity;

    invoke-direct {v2, p0, v3}, Landroid/content/ComponentName;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    invoke-virtual {v0, v2}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    .line 79
    const/high16 v2, 0x10000000

    invoke-virtual {v0, v2}, Landroid/content/Intent;->setFlags(I)Landroid/content/Intent;

    .line 80
    invoke-static {}, Lorg/ytlegacy/daviskas/app/honeycomb/Shell;->b()Z

    move-result v2

    if-nez v2, :cond_1

    .line 81
    const-string v2, "forward_intent"

    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->e()Landroid/content/Intent;

    move-result-object v3

    invoke-virtual {v0, v2, v3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;

    .line 84
    :cond_1
    const-wide v2, 0x7fffffffffffffffL

    invoke-static {v2, v3}, Lorg/ytlegacy/daviskas/app/honeycomb/Shell;->a(J)J

    .line 86
    invoke-static {v1}, Lorg/ytlegacy/daviskas/app/compat/ah;->a(Landroid/content/SharedPreferences;)Lorg/ytlegacy/daviskas/app/compat/ai;

    move-result-object v1

    const-string v2, "upgrade_prompt_shown_millis"

    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v3

    invoke-virtual {v1, v2, v3, v4}, Lorg/ytlegacy/daviskas/app/compat/ai;->a(Ljava/lang/String;J)Lorg/ytlegacy/daviskas/app/compat/ai;

    move-result-object v1

    invoke-virtual {v1}, Lorg/ytlegacy/daviskas/app/compat/ai;->a()V

    .line 89
    nop # PATCH: Убираем запуск экрана обновления

    .line 93
    :goto_1
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->finish()V

    .line 94
    return-void

    .line 71
    :cond_2
    const/4 v0, 0x0

    goto :goto_0

    .line 91
    :cond_3
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->e()Landroid/content/Intent;

    move-result-object v0

    invoke-virtual {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/o;->startActivity(Landroid/content/Intent;)V

    goto :goto_1
.end method
