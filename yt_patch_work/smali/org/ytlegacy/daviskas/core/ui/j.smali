.class public Lorg/ytlegacy/daviskas/core/ui/j;
.super Ljava/lang/Object;
.source "SourceFile"

# interfaces
.implements Landroid/view/View$OnClickListener;
.implements Lorg/ytlegacy/daviskas/core/async/m;
.implements Lorg/ytlegacy/daviskas/core/ui/h;
.implements Lorg/ytlegacy/daviskas/core/utils/v;


# instance fields
.field private final a:Lorg/ytlegacy/daviskas/core/async/az;

.field private final b:Lorg/ytlegacy/daviskas/core/e;

.field protected final c:Landroid/app/Activity;

.field final d:Landroid/os/Handler;

.field protected final e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

.field protected final f:Lorg/ytlegacy/daviskas/core/a/j;

.field private final g:Lorg/ytlegacy/daviskas/core/async/g;

.field private final h:Lorg/ytlegacy/daviskas/core/a/a;

.field private final i:Lorg/ytlegacy/daviskas/core/ui/i;

.field private j:Ljava/util/LinkedList;

.field private k:Lorg/ytlegacy/daviskas/core/async/GDataRequest;

.field private l:I

.field private m:Z

.field private n:Landroid/net/Uri;

.field private o:I

.field private p:I

.field private q:I

.field private r:I

.field private s:I


# direct methods
.method public constructor <init>(Landroid/app/Activity;Lorg/ytlegacy/daviskas/core/ui/PagedView;Lorg/ytlegacy/daviskas/core/a/a;Lorg/ytlegacy/daviskas/core/async/az;Lorg/ytlegacy/daviskas/core/e;)V
    .locals 2

    .prologue
    .line 92
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 94
    const-string v0, "activity cannot be null"

    invoke-static {p1, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/app/Activity;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->c:Landroid/app/Activity;

    .line 95
    const-string v0, "view cannot be null"

    invoke-static {p2, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lorg/ytlegacy/daviskas/core/ui/PagedView;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    .line 96
    const-string v0, "requester cannot be null"

    invoke-static {p4, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lorg/ytlegacy/daviskas/core/async/az;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->a:Lorg/ytlegacy/daviskas/core/async/az;

    .line 97
    const-string v0, "errorHelper cannot be null"

    invoke-static {p5, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lorg/ytlegacy/daviskas/core/e;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->b:Lorg/ytlegacy/daviskas/core/e;

    .line 98
    const-string v0, "targetAdapter cannot be null"

    invoke-static {p3, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lorg/ytlegacy/daviskas/core/a/a;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->h:Lorg/ytlegacy/daviskas/core/a/a;

    .line 100
    invoke-interface {p2}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->h()Lorg/ytlegacy/daviskas/core/ui/i;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->i:Lorg/ytlegacy/daviskas/core/ui/i;

    .line 101
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->i:Lorg/ytlegacy/daviskas/core/ui/i;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/i;->a()Landroid/view/View;

    move-result-object v0

    const/4 v1, 0x0

    invoke-static {p3, v0, v1}, Lorg/ytlegacy/daviskas/core/a/j;->a(Lorg/ytlegacy/daviskas/core/a/a;Landroid/view/View;Z)Lorg/ytlegacy/daviskas/core/a/j;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    .line 103
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    invoke-interface {v0, v1}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->setAdapter(Landroid/widget/ListAdapter;)V

    .line 104
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0, p0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->setOnScrollListener(Lorg/ytlegacy/daviskas/core/ui/h;)V

    .line 105
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0, p0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->setOnRetryClickListener(Landroid/view/View$OnClickListener;)V

    .line 106
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->i:Lorg/ytlegacy/daviskas/core/ui/i;

    invoke-interface {v0, p0}, Lorg/ytlegacy/daviskas/core/ui/i;->a(Landroid/view/View$OnClickListener;)V

    .line 108
    new-instance v0, Ljava/util/LinkedList;

    invoke-direct {v0}, Ljava/util/LinkedList;-><init>()V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->j:Ljava/util/LinkedList;

    .line 109
    invoke-static {p1, p0}, Lorg/ytlegacy/daviskas/core/async/g;->a(Landroid/app/Activity;Lorg/ytlegacy/daviskas/core/async/m;)Lorg/ytlegacy/daviskas/core/async/g;

    move-result-object v0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->g:Lorg/ytlegacy/daviskas/core/async/g;

    .line 111
    new-instance v0, Lorg/ytlegacy/daviskas/core/ui/k;

    invoke-virtual {p1}, Landroid/app/Activity;->getMainLooper()Landroid/os/Looper;

    move-result-object v1

    invoke-direct {v0, p0, v1}, Lorg/ytlegacy/daviskas/core/ui/k;-><init>(Lorg/ytlegacy/daviskas/core/ui/j;Landroid/os/Looper;)V

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->d:Landroid/os/Handler;

    .line 119
    return-void
.end method

.method private a(Lorg/ytlegacy/daviskas/core/async/GDataRequest;)V
    .locals 2

    .prologue
    const/4 v0, 0x1

    .line 356
    iput-boolean v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->m:Z

    .line 357
    iput-object p1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->k:Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    .line 358
    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    invoke-virtual {v1}, Lorg/ytlegacy/daviskas/core/a/j;->getWrappedAdapter()Landroid/widget/ListAdapter;

    move-result-object v1

    invoke-interface {v1}, Landroid/widget/ListAdapter;->isEmpty()Z

    move-result v1

    if-nez v1, :cond_0

    :goto_0
    if-eqz v0, :cond_1

    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->i:Lorg/ytlegacy/daviskas/core/ui/i;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/i;->b()V

    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/a/j;->b()V

    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->d()V

    .line 359
    :goto_1
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->a:Lorg/ytlegacy/daviskas/core/async/az;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->g:Lorg/ytlegacy/daviskas/core/async/g;

    invoke-interface {v0, p1, v1}, Lorg/ytlegacy/daviskas/core/async/az;->a(Ljava/lang/Object;Lorg/ytlegacy/daviskas/core/async/m;)V

    .line 360
    return-void

    .line 358
    :cond_0
    const/4 v0, 0x0

    goto :goto_0

    :cond_1
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->e()V

    goto :goto_1
.end method

.method static synthetic a(Lorg/ytlegacy/daviskas/core/ui/j;)V
    .locals 0

    .prologue
    .line 43
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->k()V

    return-void
.end method

.method private a(Ljava/lang/String;ZZ)V
    .locals 1

    .prologue
    .line 371
    if-nez p3, :cond_0

    const/4 v0, 0x1

    .line 372
    :goto_0
    if-eqz v0, :cond_1

    .line 373
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->i:Lorg/ytlegacy/daviskas/core/ui/i;

    invoke-interface {v0, p1}, Lorg/ytlegacy/daviskas/core/ui/i;->a(Ljava/lang/String;)V

    .line 374
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/a/j;->b()V

    .line 375
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->d()V

    .line 384
    :goto_1
    return-void

    .line 371
    :cond_0
    const/4 v0, 0x0

    goto :goto_0

    .line 377
    :cond_1
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->h:Lorg/ytlegacy/daviskas/core/a/a;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/a/a;->a()V

    .line 378
    if-eqz p2, :cond_2

    .line 379
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0, p1}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->b(Ljava/lang/String;)V

    goto :goto_1

    .line 381
    :cond_2
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0, p1}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->a(Ljava/lang/String;)V

    goto :goto_1
.end method

.method private k()V
    .locals 3

    .prologue
    const/4 v2, 0x0

    .line 259
    iget-boolean v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->m:Z

    if-eqz v0, :cond_1

    .line 274
    :cond_0
    :goto_0
    return-void

    .line 262
    :cond_1
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->n:Landroid/net/Uri;

    if-nez v0, :cond_2

    .line 263
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->l()V

    goto :goto_0

    .line 264
    :cond_2
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->k:Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    iget-object v0, v0, Lorg/ytlegacy/daviskas/core/async/GDataRequest;->c:Landroid/net/Uri;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->n:Landroid/net/Uri;

    if-eq v0, v1, :cond_0

    .line 265
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/a/j;->getWrappedAdapter()Landroid/widget/ListAdapter;

    move-result-object v0

    invoke-interface {v0}, Landroid/widget/ListAdapter;->getCount()I

    move-result v0

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->a()I

    move-result v1

    if-ge v0, v1, :cond_3

    .line 266
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->n:Landroid/net/Uri;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->k:Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    invoke-static {v0, v1}, Lorg/ytlegacy/daviskas/core/async/GDataRequest;->a(Landroid/net/Uri;Lorg/ytlegacy/daviskas/core/async/GDataRequest;)Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    move-result-object v0

    .line 267
    iput-object v2, p0, Lorg/ytlegacy/daviskas/core/ui/j;->n:Landroid/net/Uri;

    .line 268
    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/core/ui/j;->a(Lorg/ytlegacy/daviskas/core/async/GDataRequest;)V

    goto :goto_0

    .line 270
    :cond_3
    iput-object v2, p0, Lorg/ytlegacy/daviskas/core/ui/j;->n:Landroid/net/Uri;

    .line 271
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->b()V

    goto :goto_0
.end method

.method private l()V
    .locals 1

    .prologue
    .line 346
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->j:Ljava/util/LinkedList;

    invoke-virtual {v0}, Ljava/util/LinkedList;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 347
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->b()V

    .line 353
    :goto_0
    return-void

    .line 349
    :cond_0
    const/4 v0, 0x0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->n:Landroid/net/Uri;

    .line 350
    const/4 v0, 0x0

    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->o:I

    .line 351
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->j:Ljava/util/LinkedList;

    invoke-virtual {v0}, Ljava/util/LinkedList;->removeFirst()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/core/ui/j;->a(Lorg/ytlegacy/daviskas/core/async/GDataRequest;)V

    goto :goto_0
.end method


# virtual methods
.method protected a()V
    .locals 1

    .prologue
    .line 302
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->c()V

    .line 303
    return-void
.end method

.method public final a(Landroid/os/Bundle;)V
    .locals 1

    .prologue
    .line 138
    if-eqz p1, :cond_0

    .line 139
    const-string v0, "required_items_count"

    invoke-virtual {p1, v0}, Landroid/os/Bundle;->getInt(Ljava/lang/String;)I

    move-result v0

    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->p:I

    .line 141
    :cond_0
    return-void
.end method

.method public a(Lorg/ytlegacy/daviskas/core/async/GDataRequest;Lorg/ytlegacy/daviskas/core/model/Page;)V
    .locals 10

    .prologue
    const/4 v8, 0x0

    .line 203
    iput-boolean v8, p0, Lorg/ytlegacy/daviskas/core/ui/j;->m:Z

    .line 206
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->k:Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    if-eq p1, v0, :cond_0

    .line 256
    :goto_0
    return-void

    .line 210
    :cond_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/a/j;->getWrappedAdapter()Landroid/widget/ListAdapter;

    move-result-object v0

    invoke-interface {v0}, Landroid/widget/ListAdapter;->getCount()I

    move-result v1

    .line 211
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->a()I

    move-result v2

    .line 213
    iget v0, p2, Lorg/ytlegacy/daviskas/core/model/Page;->totalResults:I

    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->l:I

    .line 215
    iget-object v3, p2, Lorg/ytlegacy/daviskas/core/model/Page;->entries:Ljava/util/List;

    .line 216
    new-instance v4, Ljava/util/ArrayList;

    invoke-interface {v3}, Ljava/util/List;->size()I

    move-result v0

    invoke-direct {v4, v0}, Ljava/util/ArrayList;-><init>(I)V

    .line 218
    iget v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->o:I

    iget v5, p2, Lorg/ytlegacy/daviskas/core/model/Page;->startIndex:I

    sub-int/2addr v0, v5

    add-int/lit8 v0, v0, 0x1

    invoke-static {v8, v0}, Ljava/lang/Math;->max(II)I

    move-result v0

    .line 219
    invoke-interface {v3}, Ljava/util/List;->size()I

    move-result v5

    move v9, v0

    move v0, v1

    move v1, v9

    :goto_1
    if-ge v1, v5, :cond_3

    if-ge v0, v2, :cond_3

    .line 220
    invoke-interface {v3, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v6

    .line 221
    iget v7, p0, Lorg/ytlegacy/daviskas/core/ui/j;->r:I

    if-lez v7, :cond_2

    .line 222
    iget v6, p0, Lorg/ytlegacy/daviskas/core/ui/j;->r:I

    add-int/lit8 v6, v6, -0x1

    iput v6, p0, Lorg/ytlegacy/daviskas/core/ui/j;->r:I

    .line 219
    :cond_1
    :goto_2
    add-int/lit8 v1, v1, 0x1

    goto :goto_1

    .line 223
    :cond_2
    invoke-virtual {p0, v6}, Lorg/ytlegacy/daviskas/core/ui/j;->a(Ljava/lang/Object;)Z

    move-result v7

    if-eqz v7, :cond_1

    .line 224
    invoke-virtual {v4, v6}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 225
    add-int/lit8 v0, v0, 0x1

    .line 226
    iget v6, p0, Lorg/ytlegacy/daviskas/core/ui/j;->s:I

    add-int/lit8 v6, v6, 0x1

    iput v6, p0, Lorg/ytlegacy/daviskas/core/ui/j;->s:I

    goto :goto_2

    .line 229
    :cond_3
    iget v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->o:I

    iget v1, p2, Lorg/ytlegacy/daviskas/core/model/Page;->startIndex:I

    invoke-interface {v3}, Ljava/util/List;->size()I

    move-result v2

    add-int/2addr v1, v2

    add-int/lit8 v1, v1, -0x1

    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    move-result v0

    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->o:I

    .line 230
    iget v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->q:I

    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    move-result v1

    add-int/2addr v0, v1

    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->q:I

    .line 231
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "Received "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-interface {v3}, Ljava/util/List;->size()I

    move-result v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, " entries; after filtering "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    move-result v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, "; realLastIndex = "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    iget v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->o:I

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    invoke-static {}, Lorg/ytlegacy/daviskas/core/L;->d()V

    .line 234
    invoke-virtual {v4}, Ljava/util/ArrayList;->isEmpty()Z

    move-result v0

    if-nez v0, :cond_5

    .line 235
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    invoke-virtual {v0, v4}, Lorg/ytlegacy/daviskas/core/a/j;->a(Ljava/lang/Iterable;)V

    .line 236
    iget-object v0, p2, Lorg/ytlegacy/daviskas/core/model/Page;->nextUri:Landroid/net/Uri;

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->n:Landroid/net/Uri;

    .line 238
    invoke-virtual {p0, p1, v4}, Lorg/ytlegacy/daviskas/core/ui/j;->a(Lorg/ytlegacy/daviskas/core/async/GDataRequest;Ljava/util/List;)V

    .line 243
    :goto_3
    iget v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->q:I

    if-nez v0, :cond_6

    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->j:Ljava/util/LinkedList;

    invoke-virtual {v0}, Ljava/util/LinkedList;->isEmpty()Z

    move-result v0

    if-eqz v0, :cond_6

    .line 244
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->a()V

    .line 251
    :goto_4
    iget v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->s:I

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v1}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->b()I

    move-result v1

    if-lt v0, v1, :cond_4

    iget v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->q:I

    iget v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->p:I

    if-ge v0, v1, :cond_7

    .line 252
    :cond_4
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->k()V

    goto/16 :goto_0

    .line 240
    :cond_5
    const/4 v0, 0x0

    iput-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->n:Landroid/net/Uri;

    goto :goto_3

    .line 246
    :cond_6
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->i()V

    .line 247
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->d()V

    goto :goto_4

    .line 254
    :cond_7
    iput v8, p0, Lorg/ytlegacy/daviskas/core/ui/j;->s:I

    goto/16 :goto_0
.end method

.method public a(Lorg/ytlegacy/daviskas/core/async/GDataRequest;Ljava/lang/Exception;)V
    .locals 4

    .prologue
    const/4 v3, 0x0

    .line 312
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "Error for request "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, " : "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Lorg/ytlegacy/daviskas/core/L;->c(Ljava/lang/String;)V

    .line 313
    iput-boolean v3, p0, Lorg/ytlegacy/daviskas/core/ui/j;->m:Z

    .line 314
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/a/j;->getWrappedAdapter()Landroid/widget/ListAdapter;

    move-result-object v0

    invoke-interface {v0}, Landroid/widget/ListAdapter;->isEmpty()Z

    move-result v1

    .line 315
    instance-of v0, p2, Lorg/apache/http/client/HttpResponseException;

    if-eqz v0, :cond_0

    move-object v0, p2

    .line 316
    check-cast v0, Lorg/apache/http/client/HttpResponseException;

    .line 317
    invoke-virtual {v0}, Lorg/apache/http/client/HttpResponseException;->getStatusCode()I

    move-result v0

    const/16 v2, 0x193

    if-ne v0, v2, :cond_0

    .line 318
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->c:Landroid/app/Activity;

    const v2, 0x7f0a0017

    invoke-virtual {v0, v2}, Landroid/app/Activity;->getString(I)Ljava/lang/String;

    move-result-object v0

    invoke-direct {p0, v0, v3, v1}, Lorg/ytlegacy/daviskas/core/ui/j;->a(Ljava/lang/String;ZZ)V

    .line 323
    :goto_0
    return-void

    .line 322
    :cond_0
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->b:Lorg/ytlegacy/daviskas/core/e;

    invoke-virtual {v0, p2}, Lorg/ytlegacy/daviskas/core/e;->a(Ljava/lang/Throwable;)Ljava/lang/String;

    move-result-object v0

    const/4 v2, 0x1

    invoke-direct {p0, v0, v2, v1}, Lorg/ytlegacy/daviskas/core/ui/j;->a(Ljava/lang/String;ZZ)V

    goto :goto_0
.end method

.method protected a(Lorg/ytlegacy/daviskas/core/async/GDataRequest;Ljava/util/List;)V
    .locals 0

    .prologue
    .line 296
    return-void
.end method

.method public a(Lorg/ytlegacy/daviskas/core/ui/PagedView;III)V
    .locals 2

    .prologue
    .line 328
    add-int v0, p2, p3

    .line 329
    if-ne v0, p4, :cond_0

    .line 331
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->d:Landroid/os/Handler;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    .line 333
    :cond_0
    return-void
.end method

.method public final a(Lorg/ytlegacy/daviskas/core/ui/g;)V
    .locals 1

    .prologue
    .line 405
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0, p1}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->setOnPagedViewStateChangeListener(Lorg/ytlegacy/daviskas/core/ui/g;)V

    .line 406
    return-void
.end method

.method public bridge synthetic a(Ljava/lang/Object;Ljava/lang/Exception;)V
    .locals 0

    .prologue
    .line 43
    check-cast p1, Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    invoke-virtual {p0, p1, p2}, Lorg/ytlegacy/daviskas/core/ui/j;->a(Lorg/ytlegacy/daviskas/core/async/GDataRequest;Ljava/lang/Exception;)V

    return-void
.end method

.method public bridge synthetic a(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0

    .prologue
    .line 43
    check-cast p1, Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    check-cast p2, Lorg/ytlegacy/daviskas/core/model/Page;

    invoke-virtual {p0, p1, p2}, Lorg/ytlegacy/daviskas/core/ui/j;->a(Lorg/ytlegacy/daviskas/core/async/GDataRequest;Lorg/ytlegacy/daviskas/core/model/Page;)V

    return-void
.end method

.method public final varargs a([Lorg/ytlegacy/daviskas/core/async/GDataRequest;)V
    .locals 5

    .prologue
    const/4 v1, 0x0

    .line 153
    const-string v0, "requests cannot be null"

    invoke-static {p1, v0}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 154
    array-length v0, p1

    if-lez v0, :cond_0

    const/4 v0, 0x1

    :goto_0
    const-string v2, "requests cannot be empty"

    invoke-static {v0, v2}, Lorg/ytlegacy/daviskas/core/utils/u;->a(ZLjava/lang/Object;)V

    .line 155
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->c()V

    .line 156
    :goto_1
    array-length v0, p1

    if-ge v1, v0, :cond_1

    .line 157
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->j:Ljava/util/LinkedList;

    aget-object v2, p1, v1

    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "request "

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, " cannot be null"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Lorg/ytlegacy/daviskas/core/utils/u;->a(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    invoke-virtual {v0, v2}, Ljava/util/LinkedList;->add(Ljava/lang/Object;)Z

    .line 156
    add-int/lit8 v1, v1, 0x1

    goto :goto_1

    :cond_0
    move v0, v1

    .line 154
    goto :goto_0

    .line 160
    :cond_1
    invoke-direct {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->l()V

    .line 161
    return-void
.end method

.method public a(Ljava/lang/Object;)Z
    .locals 1

    .prologue
    .line 284
    const/4 v0, 0x1

    return v0
.end method

.method protected b()V
    .locals 0

    .prologue
    .line 307
    return-void
.end method

.method public final b(Ljava/lang/Object;)V
    .locals 2

    .prologue
    .line 197
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    const/4 v1, 0x0

    invoke-virtual {v0, v1, p1}, Lorg/ytlegacy/daviskas/core/a/j;->a(ILjava/lang/Object;)V

    .line 198
    iget v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->r:I

    add-int/lit8 v0, v0, 0x1

    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->r:I

    .line 199
    return-void
.end method

.method public c()V
    .locals 2

    .prologue
    const/4 v1, 0x0

    const/4 v0, 0x0

    .line 180
    iput-object v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->n:Landroid/net/Uri;

    .line 181
    iput-object v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->k:Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    .line 182
    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->o:I

    .line 183
    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->l:I

    .line 184
    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->r:I

    .line 185
    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->s:I

    .line 186
    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->q:I

    .line 187
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->j:Ljava/util/LinkedList;

    invoke-virtual {v0}, Ljava/util/LinkedList;->clear()V

    .line 188
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/a/j;->a()V

    .line 189
    return-void
.end method

.method public final d()Landroid/os/Bundle;
    .locals 3

    .prologue
    .line 132
    new-instance v0, Landroid/os/Bundle;

    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    .line 133
    const-string v1, "required_items_count"

    iget-object v2, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v2}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->j()I

    move-result v2

    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    .line 134
    return-object v0
.end method

.method public final e()V
    .locals 1

    .prologue
    .line 167
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->c()V

    .line 168
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->e()V

    .line 169
    return-void
.end method

.method public final f()V
    .locals 1

    .prologue
    .line 175
    invoke-virtual {p0}, Lorg/ytlegacy/daviskas/core/ui/j;->c()V

    .line 176
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->c()V

    .line 177
    return-void
.end method

.method public final g()V
    .locals 2

    .prologue
    .line 192
    const/4 v0, 0x0

    iput v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->p:I

    .line 193
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    iget-object v1, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    invoke-interface {v0, v1}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->setAdapter(Landroid/widget/ListAdapter;)V

    .line 194
    return-void
.end method

.method protected final h()I
    .locals 1

    .prologue
    .line 367
    iget v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->l:I

    return v0
.end method

.method protected final i()V
    .locals 2

    .prologue
    .line 398
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->f:Lorg/ytlegacy/daviskas/core/a/j;

    # PATCH: Проверка на null (иначе краш при клике на канал)
    if-eqz v0, :cond_null_adapter

    invoke-virtual {v0}, Lorg/ytlegacy/daviskas/core/a/j;->c()V

    .line 400
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->i:Lorg/ytlegacy/daviskas/core/ui/i;

    const/4 v1, 0x0

    invoke-interface {v0, v1}, Lorg/ytlegacy/daviskas/core/ui/i;->a(Ljava/lang/String;)V

    .line 401
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->i:Lorg/ytlegacy/daviskas/core/ui/i;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/i;->c()V

    :cond_null_adapter
    .line 402
    return-void
.end method

.method public final j()Lorg/ytlegacy/daviskas/core/ui/PagedView$State;
    .locals 1

    .prologue
    .line 409
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->e:Lorg/ytlegacy/daviskas/core/ui/PagedView;

    invoke-interface {v0}, Lorg/ytlegacy/daviskas/core/ui/PagedView;->f()Lorg/ytlegacy/daviskas/core/ui/PagedView$State;

    move-result-object v0

    return-object v0
.end method

.method public onClick(Landroid/view/View;)V
    .locals 1

    .prologue
    .line 337
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->k:Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    if-eqz v0, :cond_0

    .line 338
    iget-object v0, p0, Lorg/ytlegacy/daviskas/core/ui/j;->k:Lorg/ytlegacy/daviskas/core/async/GDataRequest;

    invoke-direct {p0, v0}, Lorg/ytlegacy/daviskas/core/ui/j;->a(Lorg/ytlegacy/daviskas/core/async/GDataRequest;)V

    .line 340
    :cond_0
    return-void
.end method
