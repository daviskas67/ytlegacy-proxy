.class public final Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;
.super Lorg/ytlegacy/daviskas/app/honeycomb/phone/y;
.source "SourceFile"

# interfaces
.implements Lorg/ytlegacy/daviskas/app/ui/dn;
.implements Lorg/ytlegacy/daviskas/app/ui/k;


# instance fields
.field private A:Landroid/view/View;

.field private B:Lorg/ytlegacy/daviskas/app/ui/h;

.field private C:Lorg/ytlegacy/daviskas/app/ui/bc;

.field private D:Lorg/ytlegacy/daviskas/app/ui/w;

.field private E:Landroid/view/View;

.field private F:Lorg/ytlegacy/daviskas/app/ui/h;

.field private G:Lorg/ytlegacy/daviskas/app/ui/e;

.field private final H:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;

.field private final I:Lorg/ytlegacy/daviskas/app/ui/dt;

.field private J:Z

.field private final K:Lorg/ytlegacy/daviskas/app/remote/ae;

.field private L:Lorg/ytlegacy/daviskas/app/ui/bl;

.field private M:Lorg/ytlegacy/daviskas/app/ui/bl;

.field private N:Landroid/os/Bundle;

.field private final c:Ljava/lang/String;

.field private final d:Landroid/view/LayoutInflater;

.field private final e:Landroid/content/res/Resources;

.field private final f:Lorg/ytlegacy/daviskas/core/client/bd;

.field private final g:Lorg/ytlegacy/daviskas/core/e;

.field private final h:Lorg/ytlegacy/daviskas/core/utils/q;

.field private final i:Lorg/ytlegacy/daviskas/core/client/bb;

.field private final j:Lorg/ytlegacy/daviskas/app/prefetch/e;

.field private final k:Lorg/ytlegacy/daviskas/core/async/az;

.field private final l:Lorg/ytlegacy/daviskas/core/async/az;

.field private final m:Lorg/ytlegacy/daviskas/core/Analytics;

.field private final n:Lorg/ytlegacy/daviskas/app/h;

.field private final o:Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

.field private final p:Lorg/ytlegacy/daviskas/app/honeycomb/phone/m;

.field private q:Lorg/ytlegacy/daviskas/core/model/Branding;

.field private r:Z

.field private s:Z

.field private t:Lorg/ytlegacy/daviskas/app/ui/ci;

.field private u:Lorg/ytlegacy/daviskas/app/ui/ci;

.field private v:Lorg/ytlegacy/daviskas/app/ui/ci;

.field private w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

.field private x:Landroid/view/View;

.field private y:Lorg/ytlegacy/daviskas/app/ui/h;

.field private z:Lorg/ytlegacy/daviskas/app/ui/fg;


# direct methods
.method public constructor <init>(Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;Ljava/lang/String;Lorg/ytlegacy/daviskas/app/ui/dt;)V
    .locals 8

    .prologue
    const/4 v7, 0x0

    .line 124
    invoke-direct {p0, p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/y;-><init>(Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;)V

    .line 125
    const-string v0, "channelName cannot be null"

    invoke-static {p2, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->c:Ljava/lang/String;

    .line 126
    invoke-static {p1}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->d:Landroid/view/LayoutInflater;

    .line 127
    invoke-virtual {p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;->getResources()Landroid/content/res/Resources;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->e:Landroid/content/res/Resources;

    .line 129
    invoke-virtual {p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;->getApplication()Landroid/app/Application;

    move-result-object v0

    check-cast v0, Lorg/ytlegacy/daviskas/app/YouTubeApplication;

    .line 130
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->f_()Lorg/ytlegacy/daviskas/core/client/bd;

    move-result-object v1

    iput-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->f:Lorg/ytlegacy/daviskas/core/client/bd;

    .line 131
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->k()Lorg/ytlegacy/daviskas/core/utils/q;

    move-result-object v1

    iput-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->h:Lorg/ytlegacy/daviskas/core/utils/q;

    .line 132
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->b()Lorg/ytlegacy/daviskas/core/client/bb;

    move-result-object v1

    iput-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    .line 133
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->G()Lorg/ytlegacy/daviskas/app/prefetch/e;

    move-result-object v1

    iput-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->j:Lorg/ytlegacy/daviskas/app/prefetch/e;

    .line 134
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/client/bb;->l()Lorg/ytlegacy/daviskas/core/async/az;

    move-result-object v1

    iput-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->k:Lorg/ytlegacy/daviskas/core/async/az;

    .line 135
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/client/bb;->p()Lorg/ytlegacy/daviskas/core/async/az;

    move-result-object v1

    iput-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->l:Lorg/ytlegacy/daviskas/core/async/az;

    .line 136
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->i()Lorg/ytlegacy/daviskas/core/e;

    move-result-object v1

    iput-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->g:Lorg/ytlegacy/daviskas/core/e;

    .line 137
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->h()Lorg/ytlegacy/daviskas/core/Analytics;

    move-result-object v1

    iput-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->m:Lorg/ytlegacy/daviskas/core/Analytics;

    .line 138
    invoke-virtual {p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;->V()Lorg/ytlegacy/daviskas/app/h;

    move-result-object v1

    iput-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->n:Lorg/ytlegacy/daviskas/app/h;

    .line 139
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->U()Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

    move-result-object v1

    iput-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->o:Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

    .line 140
    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/YouTubeApplication;->K()Lorg/ytlegacy/daviskas/app/remote/ae;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->K:Lorg/ytlegacy/daviskas/app/remote/ae;

    .line 141
    new-instance v0, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;

    iget-object v2, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->m:Lorg/ytlegacy/daviskas/core/Analytics;

    iget-object v3, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->o:Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;

    iget-object v4, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    iget-object v5, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->g:Lorg/ytlegacy/daviskas/core/e;

    move-object v1, p1

    move-object v6, p0

    invoke-direct/range {v0 .. v6}, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;-><init>(Landroid/app/Activity;Lorg/ytlegacy/daviskas/core/Analytics;Lorg/ytlegacy/daviskas/core/async/UserAuthorizer;Lorg/ytlegacy/daviskas/core/client/bb;Lorg/ytlegacy/daviskas/core/e;Lorg/ytlegacy/daviskas/app/ui/dn;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->H:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;

    .line 143
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->H:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;

    const-string v1, "ChannelLayer"

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;->a(Ljava/lang/String;)V

    .line 145
    iput-object p3, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->I:Lorg/ytlegacy/daviskas/app/ui/dt;

    .line 146
    iput-boolean v7, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->J:Z

    .line 148
    new-instance v0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/m;

    invoke-direct {v0, p0, v7}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/m;-><init>(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;B)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->p:Lorg/ytlegacy/daviskas/app/honeycomb/phone/m;

    .line 149
    const/4 v0, 0x0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->q:Lorg/ytlegacy/daviskas/core/model/Branding;

    .line 150
    iput-boolean v7, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->r:Z

    .line 151
    iput-boolean v7, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->s:Z

    .line 152
    return-void
.end method

.method private a(Landroid/view/ViewGroup;)Landroid/util/Pair;
    .locals 6

    .prologue
    .line 436
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->d:Landroid/view/LayoutInflater;

    const v1, 0x7f040043

    const/4 v2, 0x0

    invoke-virtual {v0, v1, p1, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    move-result-object v2

    .line 438
    new-instance v0, Lorg/ytlegacy/daviskas/app/ui/h;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    iget-object v3, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->f:Lorg/ytlegacy/daviskas/core/client/bd;

    iget-object v4, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->g:Lorg/ytlegacy/daviskas/core/e;

    iget-object v5, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->n:Lorg/ytlegacy/daviskas/app/h;

    invoke-direct/range {v0 .. v5}, Lorg/ytlegacy/daviskas/app/ui/h;-><init>(Landroid/app/Activity;Landroid/view/View;Lorg/ytlegacy/daviskas/core/client/bd;Lorg/ytlegacy/daviskas/core/e;Lorg/ytlegacy/daviskas/app/h;)V

    .line 440
    invoke-virtual {v0, p0}, Lorg/ytlegacy/daviskas/app/ui/h;->a(Lorg/ytlegacy/daviskas/app/ui/k;)V

    .line 441
    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->b(Lorg/ytlegacy/daviskas/app/ui/h;)V

    .line 443
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    if-eqz v1, :cond_0

    .line 444
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/h;->a(Lorg/ytlegacy/daviskas/core/model/UserProfile;)V

    .line 445
    iget-boolean v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->r:Z

    if-eqz v1, :cond_0

    .line 446
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->q:Lorg/ytlegacy/daviskas/core/model/Branding;

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/h;->a(Lorg/ytlegacy/daviskas/core/model/Branding;)V

    .line 449
    :cond_0
    invoke-static {v2, v0}, Landroid/util/Pair;->create(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;

    move-result-object v0

    return-object v0
.end method

.method static synthetic a(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/app/h;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->n:Lorg/ytlegacy/daviskas/app/h;

    return-object v0
.end method

.method static synthetic a(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;Lorg/ytlegacy/daviskas/core/model/UserProfile;)Lorg/ytlegacy/daviskas/core/model/UserProfile;
    .locals 0

    .prologue
    .line 60
    iput-object p1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    return-object p1
.end method

.method static synthetic a(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;Lorg/ytlegacy/daviskas/core/model/Branding;)V
    .locals 1

    .prologue
    .line 60
    iget-boolean v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->r:Z

    if-nez v0, :cond_3

    iput-object p1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->q:Lorg/ytlegacy/daviskas/core/model/Branding;

    const/4 v0, 0x1

    iput-boolean v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->r:Z

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->y:Lorg/ytlegacy/daviskas/app/ui/h;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->y:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-virtual {v0, p1}, Lorg/ytlegacy/daviskas/app/ui/h;->a(Lorg/ytlegacy/daviskas/core/model/Branding;)V

    :cond_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->B:Lorg/ytlegacy/daviskas/app/ui/h;

    if-eqz v0, :cond_1

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->B:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-virtual {v0, p1}, Lorg/ytlegacy/daviskas/app/ui/h;->a(Lorg/ytlegacy/daviskas/core/model/Branding;)V

    :cond_1
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->F:Lorg/ytlegacy/daviskas/app/ui/h;

    if-eqz v0, :cond_2

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->F:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-virtual {v0, p1}, Lorg/ytlegacy/daviskas/app/ui/h;->a(Lorg/ytlegacy/daviskas/core/model/Branding;)V

    :cond_2
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->t()V

    :cond_3
    return-void
.end method

.method static synthetic a(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;Lorg/ytlegacy/daviskas/core/ui/PagedView$State;)V
    .locals 0

    .prologue
    .line 60
    invoke-direct {p0, p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Lorg/ytlegacy/daviskas/core/ui/PagedView$State;)V

    return-void
.end method

.method private a(Lorg/ytlegacy/daviskas/app/ui/h;)V
    .locals 2

    .prologue
    .line 517
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->H:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;->e()Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;

    move-result-object v0

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->H:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;

    invoke-virtual {v1}, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;->d()Z

    move-result v1

    invoke-virtual {p1, v0, v1}, Lorg/ytlegacy/daviskas/app/ui/h;->a(Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;Z)V

    .line 519
    return-void
.end method

.method private a(Lorg/ytlegacy/daviskas/core/ui/PagedView$State;)V
    .locals 1

    .prologue
    .line 628
    iget-boolean v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->s:Z

    if-nez v0, :cond_0

    sget-object v0, Lorg/ytlegacy/daviskas/core/ui/PagedView$State;->LOADING:Lorg/ytlegacy/daviskas/core/ui/PagedView$State;

    if-eq v0, p1, :cond_0

    .line 629
    const/4 v0, 0x1

    iput-boolean v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->s:Z

    .line 630
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->t()V

    .line 632
    :cond_0
    return-void
.end method

.method static synthetic b(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/core/Analytics;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->m:Lorg/ytlegacy/daviskas/core/Analytics;

    return-object v0
.end method

.method private b(Lorg/ytlegacy/daviskas/app/ui/h;)V
    .locals 4

    .prologue
    const/4 v3, 0x1

    .line 581
    if-eqz p1, :cond_0

    .line 582
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->e:Landroid/content/res/Resources;

    const v1, 0x7f0f0005

    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getBoolean(I)Z

    move-result v0

    .line 583
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->e:Landroid/content/res/Resources;

    const v2, 0x7f100006

    invoke-virtual {v1, v2, v3, v3}, Landroid/content/res/Resources;->getFraction(III)F

    move-result v1

    .line 585
    invoke-virtual {p1, v0, v1}, Lorg/ytlegacy/daviskas/app/ui/h;->a(ZF)V

    .line 587
    :cond_0
    return-void
.end method

.method static synthetic c(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/core/model/UserProfile;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    return-object v0
.end method

.method static synthetic d(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->H:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;

    return-object v0
.end method

.method static synthetic e(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/app/ui/fg;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->z:Lorg/ytlegacy/daviskas/app/ui/fg;

    return-object v0
.end method

.method static synthetic f(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)V
    .locals 0

    .prologue
    .line 60
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->p()V

    return-void
.end method

.method static synthetic g(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Landroid/view/View;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->x:Landroid/view/View;

    return-object v0
.end method

.method static synthetic h(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/app/ui/h;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->y:Lorg/ytlegacy/daviskas/app/ui/h;

    return-object v0
.end method

.method static synthetic i(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/app/ui/bc;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->C:Lorg/ytlegacy/daviskas/app/ui/bc;

    return-object v0
.end method

.method static synthetic j(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)V
    .locals 0

    .prologue
    .line 60
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->q()V

    return-void
.end method

.method static synthetic k(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Landroid/view/View;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->A:Landroid/view/View;

    return-object v0
.end method

.method static synthetic l(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/app/ui/h;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->B:Lorg/ytlegacy/daviskas/app/ui/h;

    return-object v0
.end method

.method static synthetic m(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/app/ui/e;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->G:Lorg/ytlegacy/daviskas/app/ui/e;

    return-object v0
.end method

.method static synthetic n(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)V
    .locals 0

    .prologue
    .line 60
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->r()V

    return-void
.end method

.method static synthetic o(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Landroid/view/View;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->E:Landroid/view/View;

    return-object v0
.end method

.method private o()V
    .locals 5

    .prologue
    .line 225
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->c:Ljava/lang/String;

    iget-object v2, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    new-instance v3, Lorg/ytlegacy/daviskas/app/honeycomb/phone/n;

    const/4 v4, 0x0

    invoke-direct {v3, p0, v4}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/n;-><init>(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;B)V

    invoke-static {v2, v3}, Lorg/ytlegacy/daviskas/core/async/g;->a(Landroid/app/Activity;Lorg/ytlegacy/daviskas/core/async/m;)Lorg/ytlegacy/daviskas/core/async/g;

    move-result-object v2

    invoke-interface {v0, v1, v2}, Lorg/ytlegacy/daviskas/core/client/bb;->e(Ljava/lang/String;Lorg/ytlegacy/daviskas/core/async/m;)V

    .line 228
    return-void
.end method

.method static synthetic p(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/app/ui/h;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->F:Lorg/ytlegacy/daviskas/app/ui/h;

    return-object v0
.end method

.method private p()V
    .locals 5

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->z:Lorg/ytlegacy/daviskas/app/ui/fg;

    const/4 v1, 0x1

    new-array v1, v1, [Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    const/4 v2, 0x0

    iget-object v3, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    invoke-virtual {v3}, Lorg/ytlegacy/daviskas/core/client/bb;->a()Lorg/ytlegacy/daviskas/core/async/GDataRequestFactory;

    move-result-object v3

    iget-object v4, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    iget-object v4, v4, Lorg/ytlegacy/daviskas/core/model/UserProfile;->uploadsUri:Landroid/net/Uri;

    # PATCH: если uploadsUri отсутствует, мы полностью пропускаем инициализацию адаптера
    if-eqz v4, :cond_no_upload_uri

    invoke-virtual {v3, v4}, Lorg/ytlegacy/daviskas/core/async/GDataRequestFactory;->b(Landroid/net/Uri;)Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    move-result-object v3

    aput-object v3, v1, v2

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/fg;->a([Lorg/ytlegacy/daviskas/core/async/GDataRequest;)V

    new-instance v1, Lorg/ytlegacy/daviskas/app/honeycomb/phone/i;

    invoke-direct {v1, p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/i;-><init>(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)V

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/fg;->a(Lorg/ytlegacy/daviskas/core/ui/g;)V

    :cond_no_upload_uri
    return-void
.end method

.method static synthetic q(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/app/honeycomb/phone/m;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->p:Lorg/ytlegacy/daviskas/app/honeycomb/phone/m;

    return-object v0
.end method

.method private q()V
    .locals 5

    .prologue
    .line 340
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->C:Lorg/ytlegacy/daviskas/app/ui/bc;

    const/4 v1, 0x1

    new-array v1, v1, [Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    const/4 v2, 0x0

    iget-object v3, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    invoke-interface {v3}, Lorg/ytlegacy/daviskas/core/client/bb;->a()Lorg/ytlegacy/daviskas/core/async/GDataRequestFactory;

    move-result-object v3

    iget-object v4, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    iget-object v4, v4, Lorg/ytlegacy/daviskas/core/model/UserProfile;->playlistsUri:Landroid/net/Uri;

    invoke-virtual {v3, v4}, Lorg/ytlegacy/daviskas/core/async/GDataRequestFactory;->e(Landroid/net/Uri;)Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    move-result-object v3

    aput-object v3, v1, v2

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/bc;->a([Lorg/ytlegacy/daviskas/core/async/GDataRequest;)V

    .line 342
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->C:Lorg/ytlegacy/daviskas/app/ui/bc;

    new-instance v1, Lorg/ytlegacy/daviskas/app/honeycomb/phone/j;

    invoke-direct {v1, p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/j;-><init>(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)V

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/bc;->a(Lorg/ytlegacy/daviskas/core/ui/g;)V

    .line 353
    return-void
.end method

.method static synthetic r(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)Lorg/ytlegacy/daviskas/core/client/bb;
    .locals 1

    .prologue
    .line 60
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    return-object v0
.end method

.method private r()V
    .locals 5

    .prologue
    .line 419
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->G:Lorg/ytlegacy/daviskas/app/ui/e;

    const/4 v1, 0x1

    new-array v1, v1, [Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    const/4 v2, 0x0

    iget-object v3, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    invoke-interface {v3}, Lorg/ytlegacy/daviskas/core/client/bb;->a()Lorg/ytlegacy/daviskas/core/async/GDataRequestFactory;

    move-result-object v3

    iget-object v4, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    iget-object v4, v4, Lorg/ytlegacy/daviskas/core/model/UserProfile;->activityUri:Landroid/net/Uri;

    invoke-virtual {v3, v4}, Lorg/ytlegacy/daviskas/core/async/GDataRequestFactory;->d(Landroid/net/Uri;)Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    move-result-object v3

    aput-object v3, v1, v2

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/e;->a([Lorg/ytlegacy/daviskas/core/async/GDataRequest;)V

    .line 421
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->G:Lorg/ytlegacy/daviskas/app/ui/e;

    new-instance v1, Lorg/ytlegacy/daviskas/app/honeycomb/phone/l;

    invoke-direct {v1, p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/l;-><init>(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)V

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/e;->a(Lorg/ytlegacy/daviskas/core/ui/g;)V

    .line 433
    return-void
.end method

.method private s()V
    .locals 2

    .prologue
    .line 568
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->e:Landroid/content/res/Resources;

    const v1, 0x7f0c000f

    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getInteger(I)I

    move-result v0

    .line 569
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->t:Lorg/ytlegacy/daviskas/app/ui/ci;

    if-eqz v1, :cond_0

    .line 570
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->t:Lorg/ytlegacy/daviskas/app/ui/ci;

    invoke-virtual {v1, v0}, Lorg/ytlegacy/daviskas/app/ui/ci;->a(I)V

    .line 572
    :cond_0
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->u:Lorg/ytlegacy/daviskas/app/ui/ci;

    if-eqz v1, :cond_1

    .line 573
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->u:Lorg/ytlegacy/daviskas/app/ui/ci;

    invoke-virtual {v1, v0}, Lorg/ytlegacy/daviskas/app/ui/ci;->a(I)V

    .line 575
    :cond_1
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->v:Lorg/ytlegacy/daviskas/app/ui/ci;

    if-eqz v1, :cond_2

    .line 576
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->v:Lorg/ytlegacy/daviskas/app/ui/ci;

    invoke-virtual {v1, v0}, Lorg/ytlegacy/daviskas/app/ui/ci;->a(I)V

    .line 578
    :cond_2
    return-void
.end method

.method private t()V
    .locals 1

    .prologue
    .line 635
    iget-boolean v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->r:Z

    if-eqz v0, :cond_0

    iget-boolean v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->s:Z

    if-eqz v0, :cond_0

    .line 636
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->m()V

    .line 638
    :cond_0
    return-void
.end method


# virtual methods
.method protected final a(Lorg/ytlegacy/daviskas/core/ui/PagedListView;I)Lorg/ytlegacy/daviskas/core/a/a;
    .locals 14

    .prologue
    .line 232
    packed-switch p2, :pswitch_data_0

    .line 236
    new-instance v0, Ljava/lang/IllegalArgumentException;

    invoke-direct {v0}, Ljava/lang/IllegalArgumentException;-><init>()V

    throw v0

    .line 233
    :pswitch_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    const v1, 0x7f0a0123

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;->getString(I)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p1, v0}, Lorg/ytlegacy/daviskas/core/ui/PagedListView;->setEmptyText(Ljava/lang/CharSequence;)V

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    invoke-static {v0}, Lorg/ytlegacy/daviskas/app/ui/w;->a(Landroid/app/Activity;)Lorg/ytlegacy/daviskas/app/ui/w;

    move-result-object v13

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    invoke-static {v13, v0}, Lorg/ytlegacy/daviskas/app/ui/ab;->a(Lorg/ytlegacy/daviskas/app/ui/w;Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;)V

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->f:Lorg/ytlegacy/daviskas/core/client/bd;

    invoke-static {v0, v1, v13}, Lorg/ytlegacy/daviskas/app/adapter/bl;->a(Landroid/content/Context;Lorg/ytlegacy/daviskas/core/client/bd;Lorg/ytlegacy/daviskas/app/ui/w;)Lorg/ytlegacy/daviskas/app/adapter/bk;

    move-result-object v12

    invoke-direct {p0, p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Landroid/view/ViewGroup;)Landroid/util/Pair;

    move-result-object v1

    iget-object v0, v1, Landroid/util/Pair;->first:Ljava/lang/Object;

    check-cast v0, Landroid/view/View;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->x:Landroid/view/View;

    iget-object v0, v1, Landroid/util/Pair;->second:Ljava/lang/Object;

    check-cast v0, Lorg/ytlegacy/daviskas/app/ui/h;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->y:Lorg/ytlegacy/daviskas/app/ui/h;

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->x:Landroid/view/View;

    invoke-virtual {p1, v0}, Lorg/ytlegacy/daviskas/core/ui/PagedListView;->a(Landroid/view/View;)V

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    invoke-static {v0, v12}, Lorg/ytlegacy/daviskas/app/ui/ci;->a(Landroid/content/Context;Lorg/ytlegacy/daviskas/core/a/a;)Lorg/ytlegacy/daviskas/app/ui/ci;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->t:Lorg/ytlegacy/daviskas/app/ui/ci;

    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->s()V

    new-instance v0, Lorg/ytlegacy/daviskas/app/ui/fg;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    iget-object v3, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->t:Lorg/ytlegacy/daviskas/app/ui/ci;

    iget-object v4, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->k:Lorg/ytlegacy/daviskas/core/async/az;

    iget-object v5, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->g:Lorg/ytlegacy/daviskas/core/e;

    const/4 v6, 0x0

    iget-object v7, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->n:Lorg/ytlegacy/daviskas/app/h;

    const/4 v8, 0x0

    sget-object v9, Lorg/ytlegacy/daviskas/core/client/WatchFeature;->GUIDE_CHANNEL_UPLOADS:Lorg/ytlegacy/daviskas/core/client/WatchFeature;

    iget-object v10, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->m:Lorg/ytlegacy/daviskas/core/Analytics;

    sget-object v11, Lorg/ytlegacy/daviskas/core/Analytics$VideoCategory;->ChannelUploads:Lorg/ytlegacy/daviskas/core/Analytics$VideoCategory;

    move-object v2, p1

    invoke-direct/range {v0 .. v11}, Lorg/ytlegacy/daviskas/app/ui/fg;-><init>(Landroid/app/Activity;Lorg/ytlegacy/daviskas/core/ui/PagedView;Lorg/ytlegacy/daviskas/core/a/a;Lorg/ytlegacy/daviskas/core/async/az;Lorg/ytlegacy/daviskas/core/e;ZLorg/ytlegacy/daviskas/app/h;ZLorg/ytlegacy/daviskas/core/client/WatchFeature;Lorg/ytlegacy/daviskas/core/Analytics;Lorg/ytlegacy/daviskas/core/Analytics$VideoCategory;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->z:Lorg/ytlegacy/daviskas/app/ui/fg;

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->N:Landroid/os/Bundle;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->z:Lorg/ytlegacy/daviskas/app/ui/fg;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->N:Landroid/os/Bundle;

    const-string v2, "uploads_helper"

    invoke-virtual {v1, v2}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/fg;->a(Landroid/os/Bundle;)V

    :cond_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    if-eqz v0, :cond_1

    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->p()V

    :goto_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->K:Lorg/ytlegacy/daviskas/app/remote/ae;

    iget-object v3, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->t:Lorg/ytlegacy/daviskas/app/ui/ci;

    iget-object v4, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->g:Lorg/ytlegacy/daviskas/core/e;

    sget-object v5, Lorg/ytlegacy/daviskas/core/client/WatchFeature;->CHANNEL_UPLOAD:Lorg/ytlegacy/daviskas/core/client/WatchFeature;

    iget-object v6, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->m:Lorg/ytlegacy/daviskas/core/Analytics;

    move-object v2, v13

    invoke-static/range {v0 .. v6}, Lorg/ytlegacy/daviskas/app/ui/bl;->a(Landroid/app/Activity;Lorg/ytlegacy/daviskas/app/remote/ae;Lorg/ytlegacy/daviskas/app/ui/w;Landroid/widget/BaseAdapter;Lorg/ytlegacy/daviskas/core/e;Lorg/ytlegacy/daviskas/core/client/WatchFeature;Lorg/ytlegacy/daviskas/core/Analytics;)Lorg/ytlegacy/daviskas/app/ui/bl;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->L:Lorg/ytlegacy/daviskas/app/ui/bl;

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->L:Lorg/ytlegacy/daviskas/app/ui/bl;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/bl;->c()V

    move-object v0, v12

    .line 235
    :goto_1
    return-object v0

    .line 233
    :cond_1
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->z:Lorg/ytlegacy/daviskas/app/ui/fg;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/fg;->f()V

    goto :goto_0

    .line 234
    :pswitch_1
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    const v1, 0x7f0a011f

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;->getString(I)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p1, v0}, Lorg/ytlegacy/daviskas/core/ui/PagedListView;->setEmptyText(Ljava/lang/CharSequence;)V

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->f:Lorg/ytlegacy/daviskas/core/client/bd;

    iget-object v2, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->D:Lorg/ytlegacy/daviskas/app/ui/w;

    const/4 v3, 0x0

    invoke-static {v0, v1, v2, v3}, Lorg/ytlegacy/daviskas/app/adapter/bl;->a(Landroid/content/Context;Lorg/ytlegacy/daviskas/core/client/bd;Lorg/ytlegacy/daviskas/app/ui/w;Z)Lorg/ytlegacy/daviskas/app/adapter/bk;

    move-result-object v7

    invoke-direct {p0, p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Landroid/view/ViewGroup;)Landroid/util/Pair;

    move-result-object v1

    iget-object v0, v1, Landroid/util/Pair;->first:Ljava/lang/Object;

    check-cast v0, Landroid/view/View;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->A:Landroid/view/View;

    iget-object v0, v1, Landroid/util/Pair;->second:Ljava/lang/Object;

    check-cast v0, Lorg/ytlegacy/daviskas/app/ui/h;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->B:Lorg/ytlegacy/daviskas/app/ui/h;

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->A:Landroid/view/View;

    invoke-virtual {p1, v0}, Lorg/ytlegacy/daviskas/core/ui/PagedListView;->a(Landroid/view/View;)V

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    invoke-static {v0, v7}, Lorg/ytlegacy/daviskas/app/ui/ci;->a(Landroid/content/Context;Lorg/ytlegacy/daviskas/core/a/a;)Lorg/ytlegacy/daviskas/app/ui/ci;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->u:Lorg/ytlegacy/daviskas/app/ui/ci;

    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->s()V

    new-instance v0, Lorg/ytlegacy/daviskas/app/ui/bc;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    iget-object v3, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->u:Lorg/ytlegacy/daviskas/app/ui/ci;

    iget-object v4, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->l:Lorg/ytlegacy/daviskas/core/async/az;

    iget-object v5, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->g:Lorg/ytlegacy/daviskas/core/e;

    iget-object v6, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->n:Lorg/ytlegacy/daviskas/app/h;

    move-object v2, p1

    invoke-direct/range {v0 .. v6}, Lorg/ytlegacy/daviskas/app/ui/bc;-><init>(Landroid/app/Activity;Lorg/ytlegacy/daviskas/core/ui/PagedView;Lorg/ytlegacy/daviskas/core/a/a;Lorg/ytlegacy/daviskas/core/async/az;Lorg/ytlegacy/daviskas/core/e;Lorg/ytlegacy/daviskas/app/h;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->C:Lorg/ytlegacy/daviskas/app/ui/bc;

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->N:Landroid/os/Bundle;

    if-eqz v0, :cond_2

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->C:Lorg/ytlegacy/daviskas/app/ui/bc;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->N:Landroid/os/Bundle;

    const-string v2, "playlists_helper"

    invoke-virtual {v1, v2}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/bc;->a(Landroid/os/Bundle;)V

    :cond_2
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    if-eqz v0, :cond_3

    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->q()V

    :goto_2
    move-object v0, v7

    goto :goto_1

    :cond_3
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->C:Lorg/ytlegacy/daviskas/app/ui/bc;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/bc;->f()V

    goto :goto_2

    .line 235
    :pswitch_2
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    const v1, 0x7f0a0122

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;->getString(I)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p1, v0}, Lorg/ytlegacy/daviskas/core/ui/PagedListView;->setEmptyText(Ljava/lang/CharSequence;)V

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    invoke-static {v0}, Lorg/ytlegacy/daviskas/app/ui/w;->a(Landroid/app/Activity;)Lorg/ytlegacy/daviskas/app/ui/w;

    move-result-object v9

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    invoke-static {v9, v0}, Lorg/ytlegacy/daviskas/app/ui/ab;->b(Lorg/ytlegacy/daviskas/app/ui/w;Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;)V

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->f:Lorg/ytlegacy/daviskas/core/client/bd;

    iget-object v2, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    invoke-static {v0, v1, v2, v9}, Lorg/ytlegacy/daviskas/app/adapter/bl;->a(Landroid/content/Context;Lorg/ytlegacy/daviskas/core/client/bd;Lorg/ytlegacy/daviskas/core/client/bb;Lorg/ytlegacy/daviskas/app/ui/w;)Lorg/ytlegacy/daviskas/app/adapter/bk;

    move-result-object v8

    invoke-direct {p0, p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Landroid/view/ViewGroup;)Landroid/util/Pair;

    move-result-object v1

    iget-object v0, v1, Landroid/util/Pair;->first:Ljava/lang/Object;

    check-cast v0, Landroid/view/View;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->E:Landroid/view/View;

    iget-object v0, v1, Landroid/util/Pair;->second:Ljava/lang/Object;

    check-cast v0, Lorg/ytlegacy/daviskas/app/ui/h;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->F:Lorg/ytlegacy/daviskas/app/ui/h;

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->E:Landroid/view/View;

    invoke-virtual {p1, v0}, Lorg/ytlegacy/daviskas/core/ui/PagedListView;->a(Landroid/view/View;)V

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    invoke-static {v0, v8}, Lorg/ytlegacy/daviskas/app/ui/ci;->a(Landroid/content/Context;Lorg/ytlegacy/daviskas/core/a/a;)Lorg/ytlegacy/daviskas/app/ui/ci;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->v:Lorg/ytlegacy/daviskas/app/ui/ci;

    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->s()V

    new-instance v0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/k;

    iget-object v2, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    iget-object v4, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->v:Lorg/ytlegacy/daviskas/app/ui/ci;

    iget-object v5, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->i:Lorg/ytlegacy/daviskas/core/client/bb;

    iget-object v6, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->g:Lorg/ytlegacy/daviskas/core/e;

    const/4 v7, 0x1

    move-object v1, p0

    move-object v3, p1

    invoke-direct/range {v0 .. v7}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/k;-><init>(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;Landroid/app/Activity;Lorg/ytlegacy/daviskas/core/ui/PagedView;Lorg/ytlegacy/daviskas/core/a/a;Lorg/ytlegacy/daviskas/core/client/bb;Lorg/ytlegacy/daviskas/core/e;Z)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->G:Lorg/ytlegacy/daviskas/app/ui/e;

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->N:Landroid/os/Bundle;

    if-eqz v0, :cond_4

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->G:Lorg/ytlegacy/daviskas/app/ui/e;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->N:Landroid/os/Bundle;

    const-string v2, "events_helper"

    invoke-virtual {v1, v2}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    move-result-object v1

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/e;->a(Landroid/os/Bundle;)V

    :cond_4
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    if-eqz v0, :cond_5

    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->r()V

    :goto_3
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->K:Lorg/ytlegacy/daviskas/app/remote/ae;

    iget-object v3, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->v:Lorg/ytlegacy/daviskas/app/ui/ci;

    iget-object v4, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->g:Lorg/ytlegacy/daviskas/core/e;

    iget-object v5, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->m:Lorg/ytlegacy/daviskas/core/Analytics;

    move-object v2, v9

    invoke-static/range {v0 .. v5}, Lorg/ytlegacy/daviskas/app/ui/bl;->a(Landroid/app/Activity;Lorg/ytlegacy/daviskas/app/remote/ae;Lorg/ytlegacy/daviskas/app/ui/w;Landroid/widget/BaseAdapter;Lorg/ytlegacy/daviskas/core/e;Lorg/ytlegacy/daviskas/core/Analytics;)Lorg/ytlegacy/daviskas/app/ui/bl;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->M:Lorg/ytlegacy/daviskas/app/ui/bl;

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->M:Lorg/ytlegacy/daviskas/app/ui/bl;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/bl;->c()V

    move-object v0, v8

    goto/16 :goto_1

    :cond_5
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->G:Lorg/ytlegacy/daviskas/app/ui/e;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/e;->f()V

    goto :goto_3

    .line 232
    nop

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public final a()V
    .locals 1

    .prologue
    .line 182
    invoke-super {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/y;->a()V

    .line 183
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->s()V

    .line 184
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    if-eqz v0, :cond_0

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->H:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;->b()V

    .line 185
    :goto_0
    return-void

    .line 184
    :cond_0
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->o()V

    goto :goto_0
.end method

.method protected final a(I)V
    .locals 1

    .prologue
    .line 462
    invoke-super {p0, p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/y;->a(I)V

    .line 463
    packed-switch p1, :pswitch_data_0

    .line 477
    new-instance v0, Ljava/lang/IllegalArgumentException;

    invoke-direct {v0}, Ljava/lang/IllegalArgumentException;-><init>()V

    throw v0

    .line 465
    :pswitch_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->z:Lorg/ytlegacy/daviskas/app/ui/fg;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/fg;->j()Lorg/ytlegacy/daviskas/core/ui/PagedView$State;

    move-result-object v0

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Lorg/ytlegacy/daviskas/core/ui/PagedView$State;)V

    .line 466
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->y:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Lorg/ytlegacy/daviskas/app/ui/h;)V

    .line 475
    :goto_0
    return-void

    .line 469
    :pswitch_1
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->C:Lorg/ytlegacy/daviskas/app/ui/bc;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/bc;->j()Lorg/ytlegacy/daviskas/core/ui/PagedView$State;

    move-result-object v0

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Lorg/ytlegacy/daviskas/core/ui/PagedView$State;)V

    .line 470
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->B:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Lorg/ytlegacy/daviskas/app/ui/h;)V

    goto :goto_0

    .line 473
    :pswitch_2
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->G:Lorg/ytlegacy/daviskas/app/ui/e;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/e;->j()Lorg/ytlegacy/daviskas/core/ui/PagedView$State;

    move-result-object v0

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Lorg/ytlegacy/daviskas/core/ui/PagedView$State;)V

    .line 474
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->F:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Lorg/ytlegacy/daviskas/app/ui/h;)V

    goto :goto_0

    .line 463
    nop

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public final a(Landroid/content/res/Configuration;)V
    .locals 1

    .prologue
    .line 557
    invoke-super {p0, p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/y;->a(Landroid/content/res/Configuration;)V

    .line 558
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->D:Lorg/ytlegacy/daviskas/app/ui/w;

    if-eqz v0, :cond_0

    .line 559
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->D:Lorg/ytlegacy/daviskas/app/ui/w;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/w;->b()V

    .line 561
    :cond_0
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->s()V

    .line 562
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->y:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->b(Lorg/ytlegacy/daviskas/app/ui/h;)V

    .line 563
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->B:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->b(Lorg/ytlegacy/daviskas/app/ui/h;)V

    .line 564
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->F:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->b(Lorg/ytlegacy/daviskas/app/ui/h;)V

    .line 565
    return-void
.end method

.method public final a(Landroid/os/Bundle;)V
    .locals 3

    .prologue
    .line 158
    new-instance v0, Lorg/ytlegacy/daviskas/app/ui/w;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    invoke-direct {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/w;-><init>(Landroid/app/Activity;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->D:Lorg/ytlegacy/daviskas/app/ui/w;

    .line 159
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->D:Lorg/ytlegacy/daviskas/app/ui/w;

    const v1, 0x7f0a0255

    new-instance v2, Lorg/ytlegacy/daviskas/app/honeycomb/phone/h;

    invoke-direct {v2, p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/h;-><init>(Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;)V

    invoke-virtual {v0, v1, v2}, Lorg/ytlegacy/daviskas/app/ui/w;->a(ILorg/ytlegacy/daviskas/app/ui/aa;)I

    .line 168
    iput-object p1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->N:Landroid/os/Bundle;

    .line 169
    invoke-super {p0, p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/y;->a(Landroid/os/Bundle;)V

    .line 171
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->o()V

    .line 175
    iget-boolean v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->r:Z

    if-nez v0, :cond_0

    .line 176
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->l()V

    .line 178
    :cond_0
    return-void
.end method

.method public final a(Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;)V
    .locals 2

    .prologue
    const/4 v0, 0x0

    .line 524
    sget-object v1, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;->SUBSCRIBED:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;

    if-eq p1, v1, :cond_0

    sget-object v1, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;->NOT_SUBSCRIBED:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;

    if-ne p1, v1, :cond_1

    .line 525
    :cond_0
    iget-boolean v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->J:Z

    .line 526
    iput-boolean v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->J:Z

    move v0, v1

    .line 528
    :cond_1
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->y:Lorg/ytlegacy/daviskas/app/ui/h;

    if-eqz v1, :cond_2

    .line 529
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->y:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-direct {p0, v1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Lorg/ytlegacy/daviskas/app/ui/h;)V

    .line 531
    :cond_2
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->B:Lorg/ytlegacy/daviskas/app/ui/h;

    if-eqz v1, :cond_3

    .line 532
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->B:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-direct {p0, v1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Lorg/ytlegacy/daviskas/app/ui/h;)V

    .line 534
    :cond_3
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->F:Lorg/ytlegacy/daviskas/app/ui/h;

    if-eqz v1, :cond_4

    .line 535
    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->F:Lorg/ytlegacy/daviskas/app/ui/h;

    invoke-direct {p0, v1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a(Lorg/ytlegacy/daviskas/app/ui/h;)V

    .line 537
    :cond_4
    sget-object v1, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;->WORKING:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;

    if-ne p1, v1, :cond_6

    .line 547
    :cond_5
    :goto_0
    return-void

    .line 540
    :cond_6
    if-eqz v0, :cond_5

    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->I:Lorg/ytlegacy/daviskas/app/ui/dt;

    if-eqz v0, :cond_5

    .line 541
    sget-object v0, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;->SUBSCRIBED:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;

    if-ne p1, v0, :cond_7

    .line 542
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->I:Lorg/ytlegacy/daviskas/app/ui/dt;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->H:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;

    invoke-virtual {v1}, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;->f()Lorg/ytlegacy/daviskas/core/model/Subscription;

    move-result-object v1

    invoke-interface {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/dt;->a(Lorg/ytlegacy/daviskas/core/model/Subscription;)V

    goto :goto_0

    .line 543
    :cond_7
    sget-object v0, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;->NOT_SUBSCRIBED:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper$SubscriptionStatus;

    if-ne p1, v0, :cond_5

    .line 544
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->I:Lorg/ytlegacy/daviskas/app/ui/dt;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->w:Lorg/ytlegacy/daviskas/core/model/UserProfile;

    iget-object v1, v1, Lorg/ytlegacy/daviskas/core/model/UserProfile;->uri:Landroid/net/Uri;

    invoke-interface {v0, v1}, Lorg/ytlegacy/daviskas/app/ui/dt;->a(Landroid/net/Uri;)V

    goto :goto_0
.end method

.method protected final b(I)Ljava/lang/String;
    .locals 2

    .prologue
    .line 591
    packed-switch p1, :pswitch_data_0

    .line 595
    new-instance v0, Ljava/lang/IllegalArgumentException;

    invoke-direct {v0}, Ljava/lang/IllegalArgumentException;-><init>()V

    throw v0

    .line 592
    :pswitch_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    const v1, 0x7f0a0194

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;->getString(I)Ljava/lang/String;

    move-result-object v0

    .line 594
    :goto_0
    return-object v0

    .line 593
    :pswitch_1
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    const v1, 0x7f0a0193

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;->getString(I)Ljava/lang/String;

    move-result-object v0

    goto :goto_0

    .line 594
    :pswitch_2
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->a:Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;

    const v1, 0x7f0a0192

    invoke-virtual {v0, v1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/YouTubeActivity;->getString(I)Ljava/lang/String;

    move-result-object v0

    goto :goto_0

    .line 591
    nop

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public final b(Landroid/os/Bundle;)V
    .locals 2

    .prologue
    .line 211
    invoke-super {p0, p1}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/y;->b(Landroid/os/Bundle;)V

    .line 212
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->z:Lorg/ytlegacy/daviskas/app/ui/fg;

    if-eqz v0, :cond_0

    .line 213
    const-string v0, "uploads_helper"

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->z:Lorg/ytlegacy/daviskas/app/ui/fg;

    invoke-virtual {v1}, Lorg/ytlegacy/daviskas/app/ui/fg;->d()Landroid/os/Bundle;

    move-result-object v1

    invoke-virtual {p1, v0, v1}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 215
    :cond_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->C:Lorg/ytlegacy/daviskas/app/ui/bc;

    if-eqz v0, :cond_1

    .line 216
    const-string v0, "playlists_helper"

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->C:Lorg/ytlegacy/daviskas/app/ui/bc;

    invoke-virtual {v1}, Lorg/ytlegacy/daviskas/app/ui/bc;->d()Landroid/os/Bundle;

    move-result-object v1

    invoke-virtual {p1, v0, v1}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 218
    :cond_1
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->G:Lorg/ytlegacy/daviskas/app/ui/e;

    if-eqz v0, :cond_2

    .line 219
    const-string v0, "events_helper"

    iget-object v1, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->G:Lorg/ytlegacy/daviskas/app/ui/e;

    invoke-virtual {v1}, Lorg/ytlegacy/daviskas/app/ui/e;->d()Landroid/os/Bundle;

    move-result-object v1

    invoke-virtual {p1, v0, v1}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    .line 221
    :cond_2
    const/4 v0, 0x0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->N:Landroid/os/Bundle;

    .line 222
    return-void
.end method

.method public final c()V
    .locals 1

    .prologue
    .line 189
    invoke-super {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/y;->c()V

    .line 190
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->L:Lorg/ytlegacy/daviskas/app/ui/bl;

    if-eqz v0, :cond_0

    .line 191
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->L:Lorg/ytlegacy/daviskas/app/ui/bl;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/bl;->b()V

    .line 193
    :cond_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->M:Lorg/ytlegacy/daviskas/app/ui/bl;

    if-eqz v0, :cond_1

    .line 194
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->M:Lorg/ytlegacy/daviskas/app/ui/bl;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/bl;->b()V

    .line 196
    :cond_1
    return-void
.end method

.method public final e()V
    .locals 1

    .prologue
    .line 200
    invoke-super {p0}, Lorg/ytlegacy/daviskas/app/honeycomb/phone/y;->e()V

    .line 201
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->L:Lorg/ytlegacy/daviskas/app/ui/bl;

    if-eqz v0, :cond_0

    .line 202
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->L:Lorg/ytlegacy/daviskas/app/ui/bl;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/bl;->a()V

    .line 204
    :cond_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->M:Lorg/ytlegacy/daviskas/app/ui/bl;

    if-eqz v0, :cond_1

    .line 205
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->M:Lorg/ytlegacy/daviskas/app/ui/bl;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/bl;->a()V

    .line 207
    :cond_1
    return-void
.end method

.method public final i()V
    .locals 1

    .prologue
    .line 551
    const/4 v0, 0x1

    iput-boolean v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->J:Z

    .line 552
    iget-object v0, p0, Lorg/ytlegacy/daviskas/app/honeycomb/phone/g;->H:Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/app/ui/SubscribeHelper;->c()V

    .line 553
    return-void
.end method

.method protected final j()I
    .locals 1

    .prologue
    .line 601
    const/4 v0, 0x3

    return v0
.end method
